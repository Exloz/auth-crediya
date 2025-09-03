package co.com.bancolombia.usecase.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordEncoderPortTest {

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void shouldEncodePassword() {
        // Given
        String rawPassword = "testPassword123";
        String expectedEncoded = "$2a$10$encodedPassword";
        when(passwordEncoderPort.encodePassword(rawPassword)).thenReturn(expectedEncoded);

        // When
        String encodedPassword = passwordEncoderPort.encodePassword(rawPassword);

        // Then
        assertNotNull(encodedPassword);
        assertEquals(expectedEncoded, encodedPassword);
    }

    @Test
    void shouldMatchValidPassword() {
        // Given
        String rawPassword = "testPassword123";
        String encodedPassword = "$2a$10$encodedPassword";
        when(passwordEncoderPort.encodePassword(rawPassword)).thenReturn(encodedPassword);
        when(passwordEncoderPort.matches(rawPassword, encodedPassword)).thenReturn(true);

        // When & Then
        assertTrue(passwordEncoderPort.matches(rawPassword, encodedPassword));
    }

    @Test
    void shouldNotMatchInvalidPassword() {
        // Given
        String rawPassword = "testPassword123";
        String wrongPassword = "wrongPassword";
        String encodedPassword = "$2a$10$encodedPassword";
        when(passwordEncoderPort.encodePassword(rawPassword)).thenReturn(encodedPassword);
        when(passwordEncoderPort.matches(wrongPassword, encodedPassword)).thenReturn(false);

        // When & Then
        assertFalse(passwordEncoderPort.matches(wrongPassword, encodedPassword));
    }

    @Test
    void shouldNotMatchNullPasswords() {
        // Given
        String encodedPassword = "$2a$10$encodedPassword";
        when(passwordEncoderPort.encodePassword("test")).thenReturn(encodedPassword);
        when(passwordEncoderPort.matches(null, encodedPassword)).thenReturn(false);
        when(passwordEncoderPort.matches("test", null)).thenReturn(false);

        // When & Then
        assertFalse(passwordEncoderPort.matches(null, encodedPassword));
        assertFalse(passwordEncoderPort.matches("test", null));
    }

    @Test
    void shouldThrowExceptionForNullPassword() {
        // Given
        when(passwordEncoderPort.encodePassword(null)).thenThrow(new IllegalArgumentException("Password cannot be null or empty"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> passwordEncoderPort.encodePassword(null));
    }

    @Test
    void shouldThrowExceptionForEmptyPassword() {
        // Given
        when(passwordEncoderPort.encodePassword("")).thenThrow(new IllegalArgumentException("Password cannot be null or empty"));
        when(passwordEncoderPort.encodePassword("   ")).thenThrow(new IllegalArgumentException("Password cannot be null or empty"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> passwordEncoderPort.encodePassword(""));
        assertThrows(IllegalArgumentException.class, () -> passwordEncoderPort.encodePassword("   "));
    }
}