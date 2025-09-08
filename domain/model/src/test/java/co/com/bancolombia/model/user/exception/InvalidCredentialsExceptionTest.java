package co.com.bancolombia.model.user.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidCredentialsExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        // Given
        String message = "Invalid credentials provided";

        // When
        InvalidCredentialsException exception = new InvalidCredentialsException(message);

        // Then
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void shouldBeRuntimeException() {
        // Given
        String message = "Invalid credentials";

        // When
        InvalidCredentialsException exception = new InvalidCredentialsException(message);

        // Then
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void shouldHandleNullMessage() {
        // When
        InvalidCredentialsException exception = new InvalidCredentialsException(null);

        // Then
        assertNull(exception.getMessage());
    }

    @Test
    void shouldHandleEmptyMessage() {
        // When
        InvalidCredentialsException exception = new InvalidCredentialsException("");

        // Then
        assertEquals("", exception.getMessage());
    }
}