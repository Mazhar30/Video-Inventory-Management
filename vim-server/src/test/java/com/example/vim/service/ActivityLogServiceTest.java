package com.example.vim.service;

import com.example.vim.dto.UserActivity;
import com.example.vim.model.ActivityLog;
import com.example.vim.model.User;
import com.example.vim.model.Video;
import com.example.vim.repository.ActivityLogRepository;
import com.example.vim.util.UserAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityLogServiceTest {

    @Mock
    private ActivityLogRepository activityLogRepository;

    @Mock
    private UserService userService;

    @Mock
    private VideoService videoService;

    @InjectMocks
    private ActivityLogService activityLogService;

    private User user;
    private Video video;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("anwar");
        user.setId(5L);

        video = new Video();
        video.setId(8L);
        video.setTitle("Uploading");
        video.setDescription("upload testing");
    }

    @Test
    void testGetAllActivityLog() {
        // Arrange
        ActivityLog log1 = new ActivityLog();
        log1.setUser(user);
        log1.setVideo(video);
        log1.setAction(UserAction.VIEWED.name());
        log1.setTimestamp(LocalDateTime.now());

        ActivityLog log2 = new ActivityLog();
        log2.setUser(user);
        log2.setVideo(video);
        log2.setAction(UserAction.UPDATED.name());
        log2.setTimestamp(LocalDateTime.now());

        // Mock the repository call
        when(activityLogRepository.findAll()).thenReturn(List.of(log1, log2));

        // Act
        List<ActivityLog> logs = activityLogService.getAllActivityLog();

        // Assert
        assertNotNull(logs);
        assertEquals(2, logs.size());
        assertEquals(UserAction.VIEWED.name(), logs.get(0).getAction());
        assertEquals(UserAction.UPDATED.name(), logs.get(1).getAction());
    }

    @Test
    void testLogAction_UserNotFound() {
        // Arrange
        UserActivity userActivity = new UserActivity();
        userActivity.setUserId(5L);
        userActivity.setVideoId(8L);
        userActivity.setActivity(UserAction.VIEWED.name());

        // Mock userService to return empty
        when(userService.findUserByUsername("anwar")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            activityLogService.logAction(userActivity, "anwar");
        });
        assertEquals("User not found with user id: 5", thrown.getMessage());
    }

    @Test
    void testLogAction_VideoNotFound() {
        // Arrange
        UserActivity userActivity = new UserActivity();
        userActivity.setUserId(5L);
        userActivity.setVideoId(8L);
        userActivity.setActivity(UserAction.VIEWED.name());

        // Mock userService to return a user
        when(userService.findUserByUsername("anwar")).thenReturn(Optional.of(user));

        // Mock videoService to return empty (video not found)
        when(videoService.getVideoById(8L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            activityLogService.logAction(userActivity, "anwar");
        });
        assertEquals("Video not found with id: 8", thrown.getMessage());
    }

    @Test
    void testLogAction_Success() {
        // Arrange
        UserActivity userActivity = new UserActivity();
        userActivity.setUserId(5L);
        userActivity.setVideoId(8L);
        userActivity.setActivity(UserAction.VIEWED.name());

        // Mock userService to return a user
        when(userService.findUserByUsername("anwar")).thenReturn(Optional.of(user));

        // Mock videoService to return a video
        when(videoService.getVideoById(8L)).thenReturn(Optional.of(video));

        // Mock repository to save the log
        when(activityLogRepository.save(any(ActivityLog.class))).thenReturn(new ActivityLog());

        // Act
        activityLogService.logAction(userActivity, "anwar");

        // Assert
        verify(activityLogRepository, times(1)).save(any(ActivityLog.class));
    }
}
