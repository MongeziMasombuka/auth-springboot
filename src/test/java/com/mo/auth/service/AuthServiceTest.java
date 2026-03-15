package com.mo.auth.service;

import com.mo.auth.dto.AuthRequest;
import com.mo.auth.dto.AuthResponse;
import com.mo.auth.model.User;
import com.mo.auth.repository.UserRepository;
import com.mo.auth.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        // Assuming AuthRequest is a record: public record AuthRequest(String username, String password) {}
        authRequest = new AuthRequest("testuser", "password123");
    }

    // --- Tests for register() ---

    @Test
    void register_ShouldSaveUser_WhenUsernameIsNotTaken() {
        // Arrange
        when(userRepository.findByUsername(authRequest.username())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(authRequest.password())).thenReturn("encodedPassword123");

        // Act
        authService.register(authRequest);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("encodedPassword123", savedUser.getPassword());
    }

    @Test
    void register_ShouldThrowException_WhenUsernameIsAlreadyTaken() {
        // Arrange
        when(userRepository.findByUsername(authRequest.username())).thenReturn(Optional.of(new User()));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(authRequest)
        );

        assertEquals("Username is already taken", exception.getMessage());
        verify(userRepository, never()).save(any(User.class)); // Ensure save is never called
    }

    // --- Tests for login() ---

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        // Arrange
        UserDetails mockUserDetails = mock(UserDetails.class);
        String expectedToken = "mock.jwt.token";

        // We don't necessarily need to mock authenticationManager.authenticate returning something
        // unless you capture and use the Authentication object, but we verify it was called.
        when(userDetailsService.loadUserByUsername(authRequest.username())).thenReturn(mockUserDetails);
        when(jwtUtil.generateToken(mockUserDetails)).thenReturn(expectedToken);

        // Act
        AuthResponse response = authService.login(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals(expectedToken, response.token());

        // Verify AuthenticationManager was triggered with correct credentials
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password())
        );
    }

    @Test
    void login_ShouldThrowException_WhenCredentialsAreInvalid() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.login(authRequest));

        // Verify subsequent calls never happen
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtUtil, never()).generateToken(any());
    }
}