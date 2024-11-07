package com.example.vim.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String role;

    public UserDto(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
