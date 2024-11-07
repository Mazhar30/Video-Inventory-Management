package com.example.vim.service;

import com.example.vim.dto.VideoDto;
import com.example.vim.model.User;
import com.example.vim.model.Video;
import com.example.vim.repository.VideoRepository;
import com.example.vim.util.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private VideoService videoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initializes mocks
    }

    @Test
    void testSaveVideo() {
        // Given
        Video video = new Video();
        video.setTitle("Test Video");
        video.setDescription("Test Description");

        when(videoRepository.save(any(Video.class))).thenReturn(video);

        // When
        videoService.saveVideo(video);

        // Then
        verify(videoRepository, times(1)).save(video);
    }

    @Test
    void testGetUserVideos() {
        // Given
        Long userId = 6L;
        List<Video> videos = Arrays.asList(new Video(5L, "Video 1", "Description 1","",
                        List.of(new User(userId,"","",Role.USER))),
                new Video(6L, "Video 2", "Description 2","",
                        List.of(new User(userId,"","",Role.USER))));
        when(videoRepository.findVideosByAssignedUserId(userId)).thenReturn(videos);

        // When
        List<VideoDto> videoDtos = videoService.getUserVideos(userId);

        // Then
        assertNotNull(videoDtos);
        assertEquals(2, videoDtos.size());
    }

    @Test
    void testGetVideoById() {
        // Given
        Long videoId = 1L;
        Video video = new Video();
        video.setId(videoId);
        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));

        // When
        Optional<Video> retrievedVideo = videoService.getVideoById(videoId);

        // Then
        assertTrue(retrievedVideo.isPresent());
        assertEquals(videoId, retrievedVideo.get().getId());
    }
}
