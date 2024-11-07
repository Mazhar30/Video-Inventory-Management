package com.example.vim.controller;

import com.example.vim.dto.UserActivity;
import com.example.vim.dto.VideoDto;
import com.example.vim.model.Video;
import com.example.vim.model.User;
import com.example.vim.service.ActivityLogService;
import com.example.vim.service.UserService;
import com.example.vim.service.VideoService;
import com.example.vim.util.UserAction;
import com.example.vim.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videos")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityLogService activityLogService;

    @PostMapping
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file,
                                             @RequestParam("title") String title,
                                             @RequestParam("description") String description,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        if (userService.isCurrentUserAdmin(userDetails)) {
            if (Utils.isProperFormattedFile(file)) {
                try {
                    Video video = new Video();
                    video.setTitle(title);
                    video.setDescription(description);
                    Video savedVideo = videoService.saveVideo(video, file);
                    videoService.saveFile(file, savedVideo.getId().toString());
                    return ResponseEntity.ok("Saved Video Successfully!");
                } catch (Exception e) {
                    return ResponseEntity.internalServerError().body("Video uploading failed due to " + e.getMessage());
                }
            } else {
                return ResponseEntity.internalServerError().body("Wrong Video file format due");
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping()
    public Mono<ResponseEntity<List<VideoDto>>> getAllVideo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userService.isCurrentUserAdmin(userDetails)) {
            // Return all videos if the user is an admin
            return videoService.getAllVideos()
                    .collectList()
                    .map(ResponseEntity::ok);
        } else {
            // Return user-specific videos
            return Mono.just(ResponseEntity.ok().body(videoService.getUserVideos(userDetails.getUsername())));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoDto> getVideoById(@PathVariable Long id,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Optional<Video> videoOptional = videoService.getVideoById(id);
            if (videoOptional.isPresent()) {
                Video video = videoOptional.get();
                // Process the token and check user permissions
                if (!userService.isAuthorizedToViewVideo(userDetails, video)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }

                return ResponseEntity.ok().body(videoService.convertToDto(video));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/video/{id}")
    public ResponseEntity<Resource> getVideo(@PathVariable Long id,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Optional<Video> videoOptional = videoService.getVideoById(id);

            if (videoOptional.isPresent()) {
                Video video = videoOptional.get();
                // Process the token and check user permissions
                if (!userService.isAuthorizedToViewVideo(userDetails, video)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }

                // Get the file and media type (e.g., mp4, mkv)
                Resource videoResource = videoService.getVideoForStream(video.getVideoUrl());
                return ResponseEntity.ok()
                        .contentType(videoService.getMediaType(video.getVideoUrl()))
                        .body(videoResource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/{id}")
    public Mono<ResponseEntity<Object>> updateVideo(@RequestParam("title") String title,
                                                    @RequestParam("description") String description,
                                                    @PathVariable Long id,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        boolean isAdmin = userService.isCurrentUserAdmin(userDetails);
        String currentUsername = userDetails.getUsername();

        // Retrieve the video from the service
        Optional<Video> videoOpt = videoService.getVideoById(id);
        if (videoOpt.isPresent()) {
            Video video = videoOpt.get();

            // Check if the user is assigned to the video
            boolean isAssignedToUser = video.getAssignedUsers().stream()
                    .anyMatch(user -> user.getUsername().equals(currentUsername));

            // Admin can update title and description
            if (isAdmin) {
                video.setTitle(title);
                video.setDescription(description);
                videoService.saveVideo(video);
                return Mono.just(ResponseEntity.noContent().build());
            }
            // Assigned users can update only the description
            else if (isAssignedToUser) {
                video.setDescription(description);
                videoService.saveVideo(video);
                activityLogService.logAction(
                        new UserActivity(userService.findUserByUsername(currentUsername).get().getId(),
                                id, UserAction.UPDATED.name()), currentUsername);
                return Mono.just(ResponseEntity.noContent().build());
            }
            else {
                return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
            }
        } else {
            return Mono.just(ResponseEntity.notFound().build());
        }
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteVideo(@PathVariable Long id,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        if (userService.isCurrentUserAdmin(userDetails)) {
            return videoService.deleteVideo(id)
                    .then(Mono.just(ResponseEntity.noContent().build()));
        } else {
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
        }
    }

    @PostMapping("/{id}/assign")
    public Mono<ResponseEntity<String>> assignVideo(@PathVariable Long id,
                                                    @RequestBody List<Long> userIds,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        if (userService.isCurrentUserAdmin(userDetails)) {
            Optional<Video> videoOptional = videoService.getVideoById(id);
            if (videoOptional.isPresent()) {
                Video video = videoOptional.get();
                List<Long> assignedUserIds = video.getAssignedUsers().stream()
                        .map(User::getId)
                        .toList();

                // Filter out the already assigned users from the userIds list
                List<Long> usersToAssign = userIds.stream()
                        .filter(userId -> !assignedUserIds.contains(userId))
                        .toList();

                // If there are no users to assign, return a message
                if (usersToAssign.isEmpty()) {
                    return Mono.just(ResponseEntity.ok("All users are already assigned to the video"));
                }

                // Assign video to the users
                videoService.assignVideoToUsers(video, usersToAssign);
                return (Mono.just(ResponseEntity.ok("Video assigned")));
            } else {
                return Mono.error(new RuntimeException("Video not found with id " + id));
            }
        } else {
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
        }
    }
}

