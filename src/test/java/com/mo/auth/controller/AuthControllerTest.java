package com.mo.auth.controller;

import com.mo.auth.dto.AuthRequest;
import com.mo.auth.dto.AuthResponse;
import com.mo.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private AuthRequest validRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        // Use the record constructor directly
        validRequest = new AuthRequest("testuser", "password123");
        authResponse = new AuthResponse("jwt-token");
    }

    @Test
    void register_ValidRequest_ReturnsCreatedStatusWithMessage() {
        // Act
        ResponseEntity<String> response = authController.register(validRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("User registered successfully");
        verify(authService).register(validRequest);
    }

    @Test
    void register_ServiceThrowsException_PropagatesException() {
        // Arrange
        doThrow(new RuntimeException("Service error")).when(authService).register(any(AuthRequest.class));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authController.register(validRequest));
        assertThat(exception.getMessage()).isEqualTo("Service error");
        verify(authService).register(validRequest);
    }

    @Test
    void login_ValidRequest_ReturnsOkWithAuthResponse() {
        // Arrange
        when(authService.login(validRequest)).thenReturn(authResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.login(validRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(authResponse);
        verify(authService).login(validRequest);
    }

    @Test
    void login_ServiceThrowsException_PropagatesException() {
        // Arrange
        when(authService.login(any(AuthRequest.class))).thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authController.login(validRequest));
        assertThat(exception.getMessage()).isEqualTo("Invalid credentials");
        verify(authService).login(validRequest);
    }
}