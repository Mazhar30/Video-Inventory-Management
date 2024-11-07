package com.example.vim.controller;

import com.example.vim.dto.UserActivity;
import com.example.vim.model.ActivityLog;
import com.example.vim.service.ActivityLogService;
import com.example.vim.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity-log")
public class ActivityLogController {

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<List<ActivityLog>> getActivityLogs(@AuthenticationPrincipal UserDetails userDetails) {
        if (userService.isCurrentUserAdmin(userDetails)) {
            return ResponseEntity.ok(activityLogService.getAllActivityLog());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping
    public ResponseEntity<String> addActivityLog(@RequestBody UserActivity activity,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        if (!userService.isCurrentUserAdmin(userDetails)) {
            activityLogService.logAction(activity, userDetails.getUsername());
            return ResponseEntity.ok().body("Successfully added activity");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
