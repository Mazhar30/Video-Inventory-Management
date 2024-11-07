package com.example.vim.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // Many ActivityLogs can belong to one User
    @JoinColumn(name = "user_id", nullable = false) // This specifies the foreign key column in the table
    private User user;

    @ManyToOne // Many ActivityLogs can belong to one Video
    @JoinColumn(name = "video_id", nullable = false) // This specifies the foreign key column in the table
    private Video video;

    private String action; // e.g., viewed or updated

    private LocalDateTime timestamp;
}

