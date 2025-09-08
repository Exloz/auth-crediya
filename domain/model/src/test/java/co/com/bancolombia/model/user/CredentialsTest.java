package co.com.bancolombia.model.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CredentialsTest {

    @Test
    void shouldCreateCredentialsWithBuilder() {
        // Given
        Long userId = 1L;
        String password = "$2a$10$hashedPassword";

        // When
        Credentials credentials = Credentials.builder()
            .userId(userId)
            .password(password)
            .build();

        // Then
        assertEquals(userId, credentials.getUserId());
        assertEquals(password, credentials.getPassword());
    }

    @Test
    void shouldCreateCredentialsWithNoArgsConstructor() {
        // When
        Credentials credentials = new Credentials();

        // Then
        assertNull(credentials.getUserId());
        assertNull(credentials.getPassword());
    }

    @Test
    void shouldCreateCredentialsWithAllArgsConstructor() {
        // Given
        Long userId = 1L;
        String password = "$2a$10$hashedPassword";

        // When
        Credentials credentials = new Credentials(userId, password);

        // Then
        assertEquals(userId, credentials.getUserId());
        assertEquals(password, credentials.getPassword());
    }

    @Test
    void shouldUseToBuilder() {
        // Given
        Credentials originalCredentials = Credentials.builder()
            .userId(1L)
            .password("$2a$10$oldPassword")
            .build();

        // When
        Credentials updatedCredentials = originalCredentials.toBuilder()
            .password("$2a$10$newPassword")
            .build();

        // Then
        assertEquals(1L, updatedCredentials.getUserId());
        assertEquals("$2a$10$newPassword", updatedCredentials.getPassword());
    }

    @Test
    void shouldHandleNullValues() {
        // When
        Credentials credentials = Credentials.builder().build();

        // Then
        assertNull(credentials.getUserId());
        assertNull(credentials.getPassword());
    }

    @Test
    void shouldHaveSameValues() {
        // Given
        Credentials credentials1 = Credentials.builder()
            .userId(1L)
            .password("$2a$10$password")
            .build();

        Credentials credentials2 = Credentials.builder()
            .userId(1L)
            .password("$2a$10$password")
            .build();

        // Then
        assertEquals(credentials1.getUserId(), credentials2.getUserId());
        assertEquals(credentials1.getPassword(), credentials2.getPassword());
    }

    @Test
    void shouldHaveDifferentValues() {
        // Given
        Credentials credentials1 = Credentials.builder()
            .userId(1L)
            .password("$2a$10$password1")
            .build();

        Credentials credentials2 = Credentials.builder()
            .userId(1L)
            .password("$2a$10$password2")
            .build();

        // Then
        assertNotEquals(credentials1.getPassword(), credentials2.getPassword());
    }
}