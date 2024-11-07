package com.example.vim.service;

import com.example.vim.dto.UserActivity;
import com.example.vim.model.ActivityLog;
import com.example.vim.model.User;
import com.example.vim.model.Video;
import com.example.vim.repository.ActivityLogRepository;
import com.example.vim.util.UserAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityLogService {
    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private VideoService videoService;

    public List<ActivityLog> getAllActivityLog() {
        return activityLogRepository.findAll();
    }

    public void logAction(UserActivity userActivity, String username) {
        Optional<User> user = userService.findUserByUsername(username);
        if (user.isPresent()) {
            Optional<Video> video = videoService.getVideoById(userActivity.getVideoId());
            if (video.isPresent()) {
                ActivityLog log = new ActivityLog();
                log.setUser(user.get());
                log.setVideo(video.get());
                log.setAction(UserAction.valueOf(userActivity.getActivity()).name());
                log.setTimestamp(LocalDateTime.now());

                activityLogRepository.save(log);
            } else {
               throw new RuntimeException("Video not found with id: " + userActivity.getVideoId());
            }
        } else {
            throw new RuntimeException("User not found with user id: " + userActivity.getUserId());
        }
    }

}

