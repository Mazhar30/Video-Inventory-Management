package com.example.vim.controller;

import com.example.vim.dto.UserDto;
import com.example.vim.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register(@RequestBody UserDto authRequest) {
        // Check if the user already exists
        if (userService.findUserByUsername(authRequest.getUsername()).isPresent()) {
            // If user already exists, return a 400 response
            return Mono.just(ResponseEntity.status(400).body("User already exists"));
        } else {
            userService.registerUser(authRequest);
            return Mono.just(ResponseEntity.ok("User registered successfully"));
        }
    }

    @GetMapping()
    public Mono<ResponseEntity<List<UserDto>>> getAllUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userService.isCurrentUserAdmin(userDetails)) {
            return Mono.just(ResponseEntity.ok(userService.getAllGeneralUser()));
        } else {
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
        }
    }
}
