package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.exception.UserUnauthorizedException;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserRequest validRequest;
    private User existingUser;

    @BeforeEach
    void setUp() {
        validRequest = new UserRequest("Steven", "password123");
        existingUser = new User();
        existingUser.setName("Steven");
        existingUser.setPassword("password123");
    }

    @Test
    void registerUser_Success() {
        when(userRepository.existsByName(validRequest.getName())).thenReturn(false);
        userService.registerUser(validRequest);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_ThrowsException_WhenUserExists() {
        when(userRepository.existsByName(validRequest.getName())).thenReturn(true);
        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(validRequest));
    }

    @Test
    void loginUser_Success() {
        when(userRepository.findByName(validRequest.getName())).thenReturn(Optional.of(existingUser));
        when(jwtService.generateTokens(validRequest.getName())).thenReturn(Map.of("accessToken", "access123", "refreshToken", "refresh123"));

        Map<String, String> tokens = userService.loginUser(validRequest);

        assertEquals("access123", tokens.get("accessToken"));
        assertEquals("refresh123", tokens.get("refreshToken"));
    }

    @Test
    void loginUser_ThrowsException_InvalidPassword() {
        User userWithWrongPassword = new User();
        userWithWrongPassword.setName("Steven");
        userWithWrongPassword.setPassword("wrongPassword");

        when(userRepository.findByName(validRequest.getName())).thenReturn(Optional.of(userWithWrongPassword));

        assertThrows(UserUnauthorizedException.class, () -> userService.loginUser(validRequest));
    }

    @Test
    void generateNewAccessToken_Success() {
        when(jwtService.generateNewAccessToken("validRefreshToken")).thenReturn("newAccessToken");
        String token = userService.generateNewAccessToken("validRefreshToken");
        assertEquals("newAccessToken", token);
    }

    @Test
    void checkIfNameExists_Success() {
        when(userRepository.findByName("Steven")).thenReturn(Optional.of(existingUser));
        User user = userService.checkIfNameExists("Steven");
        assertEquals("Steven", user.getName());
    }

    @Test
    void checkIfNameExists_ThrowsException_WhenUserNotFound() {
        when(userRepository.findByName("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.checkIfNameExists("unknown"));
    }
}