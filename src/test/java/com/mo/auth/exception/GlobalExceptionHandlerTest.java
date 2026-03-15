package com.mo.auth.exception;

import com.mo.auth.dto.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleBadRequest_ShouldReturn400() {
        String errorMessage = "Username already exists";
        IllegalArgumentException ex = new IllegalArgumentException(errorMessage);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadRequest(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        // Note: Using .message() instead of .getMessage()
        assertThat(Objects.requireNonNull(response.getBody()).message()).isEqualTo(errorMessage);
        assertThat(response.getBody().status()).isEqualTo(400);
    }

    @Test
    void handleBadCredentials_ShouldReturn401() {
        BadCredentialsException ex = new BadCredentialsException("Wrong pass");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadCredentials(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(Objects.requireNonNull(response.getBody()).message()).isEqualTo("Invalid username or password");
    }

    @Test
    void handleInvalidSignature_ShouldReturn401() {
        SignatureException ex = new SignatureException("Invalid signature");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidSignature(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().message()).isEqualTo("Invalid token signature");
    }

    @Test
    void handleExpiredToken_ShouldReturn401() {
        // ExpiredJwtException constructor requirements
        ExpiredJwtException ex = new ExpiredJwtException(null, null, "Expired");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleExpiredToken(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().message()).isEqualTo("Token has expired");
    }

    @Test
    void handleGlobalException_ShouldReturn500() {
        Exception ex = new RuntimeException("Internal Error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().timestamp()).isNotNull();
    }
}