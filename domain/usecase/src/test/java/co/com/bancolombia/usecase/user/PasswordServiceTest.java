package co.com.bancolombia.usecase.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderPortTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final PasswordEncoderPort passwordEncoderPort = new co.com.bancolombia.api.config.BCryptPasswordEncoderService(passwordEncoder);

    @Test
    void shouldEncodePassword() {
        // Given
        String rawPassword = "testPassword123";

        // When
        String encodedPassword = passwordEncoderPort.encodePassword(rawPassword);

        // Then
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(encodedPassword.startsWith("$2a$")); // BCrypt format
    }

    @Test
    void shouldMatchValidPassword() {
        // Given
        String rawPassword = "testPassword123";
        String encodedPassword = passwordEncoderPort.encodePassword(rawPassword);

        // When & Then
        assertTrue(passwordEncoderPort.matches(rawPassword, encodedPassword));
    }

    @Test
    void shouldNotMatchInvalidPassword() {
        // Given
        String rawPassword = "testPassword123";
        String wrongPassword = "wrongPassword";
        String encodedPassword = passwordEncoderPort.encodePassword(rawPassword);

        // When & Then
        assertFalse(passwordEncoderPort.matches(wrongPassword, encodedPassword));
    }

    @Test
    void shouldNotMatchNullPasswords() {
        // Given
        String encodedPassword = passwordEncoderPort.encodePassword("test");

        // When & Then
        assertFalse(passwordEncoderPort.matches(null, encodedPassword));
        assertFalse(passwordEncoderPort.matches("test", null));
    }

    @Test
    void shouldThrowExceptionForNullPassword() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> passwordEncoderPort.encodePassword(null));
    }

    @Test
    void shouldThrowExceptionForEmptyPassword() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> passwordEncoderPort.encodePassword(""));
        assertThrows(IllegalArgumentException.class, () -> passwordEncoderPort.encodePassword("   "));
    }
}