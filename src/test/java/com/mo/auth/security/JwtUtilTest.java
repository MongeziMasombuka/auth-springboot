package com.mo.auth.security;

import org.junit.jupiter.api.Test;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;
    // Must be at least 32 characters for HS256
    private final String testSecret = "myUltraSecretKeyThatIsAtLeast32CharactersLong!!";
    private final long testExpiration = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Manually inject @Value fields
        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", testExpiration);

        userDetails = new User("testuser", "password", Collections.emptyList());
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        String token = jwtUtil.generateToken(userDetails);

        assertNotNull(token);
        assertEquals("testuser", jwtUtil.extractUsername(token));
    }

    @Test
    void isTokenValid_ShouldReturnTrue_ForCorrectUserAndNotExpired() {
        String token = jwtUtil.generateToken(userDetails);

        assertTrue(jwtUtil.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_ShouldReturnFalse_ForDifferentUser() {
        String token = jwtUtil.generateToken(userDetails);
        UserDetails differentUser = new User("stranger", "password", Collections.emptyList());

        assertFalse(jwtUtil.isTokenValid(token, differentUser));
    }

    @Test
    void extractClaim_ShouldWorkForExpirationDate() {
        String token = jwtUtil.generateToken(userDetails);
        Date expiration = jwtUtil.extractClaim(token, io.jsonwebtoken.Claims::getExpiration);

        assertTrue(expiration.after(new Date()));
    }

    @Test
    void validateToken_ShouldThrowException_WhenSecretIsTampered() {
        String token = jwtUtil.generateToken(userDetails);

        // Setup a second JwtUtil with a different secret
        JwtUtil malformedUtil = new JwtUtil();
        ReflectionTestUtils.setField(malformedUtil, "secret", "aDifferentSecretKeyAltogether12345678");
        ReflectionTestUtils.setField(malformedUtil, "expirationMs", testExpiration);

        // Parsing a token signed with a different key should throw an exception
        assertThrows(SignatureException.class, () -> malformedUtil.extractUsername(token));
    }

    @Test
    void isTokenExpired_ShouldReturnTrue_WhenTokenIsOld() {
        // Set expiration to a negative value to simulate an already expired token
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", -1000L);
        String token = jwtUtil.generateToken(userDetails);

        // Note: isTokenValid checks expiration internally
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> {
            jwtUtil.isTokenValid(token, userDetails);
        });
    }
}