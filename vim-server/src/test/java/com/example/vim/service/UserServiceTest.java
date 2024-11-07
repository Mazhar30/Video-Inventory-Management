package com.example.vim.service;

import com.example.vim.dto.UserDto;
import com.example.vim.model.User;
import com.example.vim.model.Video;
import com.example.vim.repository.UserRepository;
import com.example.vim.util.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setRole(Role.USER);

        when(userRepository.save(user)).thenReturn(user);

        userService.saveUser(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testFindUserByUsername() {
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findUserByUsername(username);

        assertTrue(foundUser.isPresent());
        assertEquals(username, foundUser.get().getUsername());
    }

    @Test
    void testFindUserById() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findUserById(userId);

        assertTrue(foundUser.isPresent());
        assertEquals(userId, foundUser.get().getId());
    }

    @Test
    void testGetAllGeneralUser() {
        User user1 = new User();
        user1.setId(5L);
        user1.setUsername("anwar");
        user1.setRole(Role.USER);

        User user2 = new User();
        user2.setId(6L);
        user2.setUsername("waleed");
        user2.setRole(Role.USER);

        when(userRepository.findAllByRole(Role.USER)).thenReturn(List.of(user1, user2));

        List<UserDto> userDtos = userService.getAllGeneralUser();

        assertEquals(2, userDtos.size());
        assertEquals("anwar", userDtos.get(0).getUsername());
        assertEquals("waleed", userDtos.get(1).getUsername());
    }

    @Test
    void testRegisterUser() {
        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setPassword("testPassword");
        userDto.setRole("USER");

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword("encodedPassword");
        user.setRole(Role.valueOf(userDto.getRole()));

        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.registerUser(userDto);

        verify(userRepository, times(1)).save(any(User.class));
    }
}