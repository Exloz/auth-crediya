package co.com.bancolombia.model.user.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserAlreadyExistsExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        // Given
        String message = "User already exists";

        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException(message);

        // Then
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void shouldBeRuntimeException() {
        // Given
        String message = "User already exists";

        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException(message);

        // Then
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void shouldHandleNullMessage() {
        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException(null);

        // Then
        assertNull(exception.getMessage());
    }

    @Test
    void shouldHandleEmptyMessage() {
        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException("");

        // Then
        assertEquals("", exception.getMessage());
    }
}