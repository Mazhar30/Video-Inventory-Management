package com.example.vim.service;

import com.example.vim.dto.UserDto;
import com.example.vim.model.User;
import com.example.vim.model.Video;
import com.example.vim.repository.UserRepository;
import com.example.vim.util.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public void saveUser(User user) {
        Mono.just(userRepository.save(user));
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<UserDto> getAllGeneralUser() {
        return userRepository.findAllByRole(Role.USER).stream()
                .map(user -> new UserDto(user.getId(), user.getUsername()))
                .collect(Collectors.toList());
    }


    public List<User> findUserByIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public boolean isCurrentUserAdmin(UserDetails userDetails) {
        if (userDetails != null) {
            return userDetails.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        }

        return false;
    }

    public void registerUser(UserDto user) {
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setRole(Role.valueOf(user.getRole()!=null ? user.getRole() : Role.USER.name()));
        saveUser(newUser);
    }

    public boolean isAuthorizedToViewVideo(UserDetails userDetails, Video video) {
        boolean isAdmin = isCurrentUserAdmin(userDetails);
        boolean isAssignedToUser = video.getAssignedUsers().stream()
                .anyMatch(user -> user.getUsername().equals(userDetails.getUsername()));
        return isAdmin || isAssignedToUser;
    }

}

