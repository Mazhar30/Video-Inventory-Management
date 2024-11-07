package com.example.vim.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VideoDto {

    private Long id;

    private String title;
    private String description;
    private String videoUrl;
    private List<Long> assignedUsers;
}
