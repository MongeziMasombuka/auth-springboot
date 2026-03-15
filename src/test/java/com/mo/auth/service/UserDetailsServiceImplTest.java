package com.mo.auth.service;

import static org.junit.jupiter.api.Assertions.*;

import com.mo.auth.model.User;
import com.mo.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;
    private final String USERNAME = "testuser";
    private final String PASSWORD = "encodedPassword";

    @BeforeEach
    void setUp() {
        // Create a User instance using setters (assuming User has default constructor and setters)
        testUser = new User();
        testUser.setUsername(USERNAME);
        testUser.setPassword(PASSWORD);
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        // Arrange
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(USERNAME);

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(USERNAME);
        assertThat(userDetails.getPassword()).isEqualTo(PASSWORD);
        assertThat(userDetails.getAuthorities()).isEmpty();
        verify(userRepository, times(1)).findByUsername(USERNAME);
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsUsernameNotFoundException() {
        // Arrange
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(USERNAME))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining(USERNAME);

        verify(userRepository, times(1)).findByUsername(USERNAME);
    }

    @Test
    void loadUserByUsername_NullUsername_PropagatesRepositoryException() {
        // Arrange
        when(userRepository.findByUsername(null)).thenThrow(new IllegalArgumentException("Username cannot be null"));

        // Act & Assert
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username cannot be null");

        verify(userRepository, times(1)).findByUsername(null);
    }
}