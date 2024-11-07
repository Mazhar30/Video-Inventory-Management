package com.example.vim.controller;

import com.example.vim.dto.UserDto;
import com.example.vim.repository.TokenRepository;
import com.example.vim.security.JwtUtil;
import com.example.vim.service.UserService;
import com.example.vim.util.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserDto authRequest) {
        // Prepare the response
        Map<String, String> response = new HashMap<>();
        try {
            System.out.println("Attempting login for user: " + authRequest.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            // Check for an existing token
            String existingToken = tokenRepository.getToken(authRequest.getUsername());
            if (existingToken != null) {
                // Optionally log or handle the existing token
                System.out.println("Existing token found for user: " + authRequest.getUsername() + ", invalidating it.");
                tokenRepository.deleteToken(authRequest.getUsername()); // Invalidate the existing token
            }

            // Generate the JWT token on success
            String token = jwtUtil.generateToken(authentication.getName());

            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse(Role.USER.name()); // Default role if none found

            response.put("token", token);
            response.put("role", role.equals("ROLE_ADMIN") ? Role.ADMIN.name() : Role.USER.name());

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
            response.put("error", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        String jwt = authHeader.substring(7); // Remove "Bearer " prefix
        String username = jwtUtil.extractUsername(jwt);

        // Check if the user has a valid token in Redis
        String token = tokenRepository.getToken(username);
        if (token != null) {
            tokenRepository.deleteToken(username);
            return ResponseEntity.ok("User logged out successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in");
        }
    }

    @GetMapping("/role")
    public ResponseEntity<String> getRole(@AuthenticationPrincipal UserDetails userDetails) {
        String role = userService.isCurrentUserAdmin(userDetails) ? Role.ADMIN.name() : Role.USER.name();
        return ResponseEntity.ok().body(role);
    }
}