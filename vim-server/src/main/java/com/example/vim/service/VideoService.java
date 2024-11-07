package com.example.vim.service;

import com.example.vim.dto.VideoDto;
import com.example.vim.model.User;
import com.example.vim.model.Video;
import com.example.vim.repository.VideoRepository;
import com.example.vim.util.Utils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VideoService {

    @Value("${video.storage.path}")
    private String videoStoragePath;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserService userService;

    public void saveVideo(Video video) {
        videoRepository.save(video);
    }

    public Video saveVideo(Video video, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = StringUtils.getFilenameExtension(originalFilename);
        Video newVideo = videoRepository.save(video);
        newVideo.setVideoUrl(newVideo.getId()+"."+fileExtension);
        return videoRepository.save(newVideo);
    }

    public Flux<VideoDto> getAllVideos() {
        return Flux.fromIterable(videoRepository.findAll())
                .map(this::convertToDto);
    }


    public Optional<Video> getVideoById(Long id) {
        return videoRepository.findById(id);
    }

    public Mono<Void> deleteVideo(Long id) {
        Optional<Video> video = videoRepository.findById(id);
        if (video.isPresent()) {
            videoRepository.deleteById(id);
            deleteVideoFile(video.get().getVideoUrl());
        }
        return Mono.empty();
    }

    public List<VideoDto> getUserVideos(String username) {
        Optional<User> user = userService.findUserByUsername(username);
        if (user.isPresent()) {
            return getUserVideos(user.get().getId());
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public List<VideoDto> getUserVideos(Long userId) {
        return videoRepository.findVideosByAssignedUserId(userId).stream().map(this::convertToDto).toList();
    }

    public void deleteVideoFile(String fileNameWithExtension) {
        Path videoFilePath = Paths.get(videoStoragePath + fileNameWithExtension);
        try {
            if (Files.exists(videoFilePath)) {
                Files.delete(videoFilePath);
                System.out.println("File deleted successfully: " + fileNameWithExtension);
            } else {
                System.out.println("File not found: " + fileNameWithExtension);
            }
        } catch (IOException e) {
            System.err.println("Error deleting file: " + e.getMessage());
        }
    }

    public void saveFile(MultipartFile file, String fileNameWithoutExtension) {
        String fileName = fileNameWithoutExtension + "." + Utils.getFileExtension(file);

        File videoDirectory = new File(videoStoragePath);
        if (!videoDirectory.exists()) {
            videoDirectory.mkdirs();
        }

        File videoFile = new File(videoDirectory, fileName);
        try {
            file.transferTo(videoFile);
        } catch (IOException e) {
            deleteVideo(Long.parseLong(fileNameWithoutExtension));
            throw new RuntimeException("Failed to save video file", e);
        }
    }

    public void assignVideoToUsers(Video video, List<Long> userIds) {

        List<User> users = userService.findUserByIds(userIds);

        List<User> existingAssignedUsers = video.getAssignedUsers();
        existingAssignedUsers.addAll(users);
        video.setAssignedUsers(existingAssignedUsers);

        saveVideo(video);
    }

    public VideoDto convertToDto(Video video) {
        VideoDto videoDto = new VideoDto();
        videoDto.setId(video.getId());
        videoDto.setTitle(video.getTitle());
        videoDto.setDescription(video.getDescription());
        videoDto.setVideoUrl(video.getVideoUrl());

        List<Long> assignedUserIds = video.getAssignedUsers().stream()
                .map(User::getId)
                .collect(Collectors.toList());

        videoDto.setAssignedUsers(assignedUserIds);
        return videoDto;
    }

    public Resource getVideoForStream(String videoUrl) {
        File videoFile = new File(videoStoragePath + videoUrl);
        if (!videoFile.exists()) {
            throw new RuntimeException("Video not found");
        }
        return new FileSystemResource(videoFile);
    }

    public MediaType getMediaType(String videoUrl) throws IOException {
        File videoFile = new File(videoStoragePath + videoUrl);
        String mimeType = Files.probeContentType(videoFile.toPath());

        // If probeContentType returns null, fall back to MIME type based on file extension
        if (mimeType == null) {
            String fileExtension = videoUrl.substring(videoUrl.lastIndexOf(".") + 1).toLowerCase();
            return switch (fileExtension) {
                case "mp4" -> MediaType.valueOf("video/mp4");
                case "mkv" -> MediaType.valueOf("video/x-matroska");
                case "webm" -> MediaType.valueOf("video/webm");
                default -> MediaType.APPLICATION_OCTET_STREAM;
            };
        }
        return MediaType.valueOf(mimeType);
    }
}

