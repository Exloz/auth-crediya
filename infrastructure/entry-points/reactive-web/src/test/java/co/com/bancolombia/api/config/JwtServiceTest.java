package co.com.bancolombia.api.config;

import co.com.bancolombia.api.config.jwt.JwtService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws IOException {
        jwtService = new JwtService();

        // Set test configuration values
        ReflectionTestUtils.setField(jwtService, "jwtIssuer", "test-issuer");
        ReflectionTestUtils.setField(jwtService, "jwtExpiry", 3600000L);

        // Use the generated keys for testing
        Path privateKeyPath = Paths.get("src/test/resources/keys/private.pem");
        Path publicKeyPath = Paths.get("src/test/resources/keys/public.pem");

        if (Files.exists(privateKeyPath) && Files.exists(publicKeyPath)) {
            ReflectionTestUtils.setField(jwtService, "privateKeyPath", privateKeyPath.toString());
            ReflectionTestUtils.setField(jwtService, "publicKeyPath", publicKeyPath.toString());

            // Initialize keys
            jwtService.initKeys();
        } else {
            fail("RSA key files not found. Please generate keys first.");
        }
    }

    @Test
    void shouldGenerateValidToken() {
        // Given
        String email = "test@example.com";
        Long userId = 123L;
        String role = "USER";

        // When
        String token = jwtService.generateToken(email, userId, role);

        // Then
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void shouldValidateValidToken() {
        // Given
        String email = "test@example.com";
        Long userId = 123L;
        String role = "USER";
        String token = jwtService.generateToken(email, userId, role);

        // When
        Claims claims = jwtService.validateToken(token);

        // Then
        assertNotNull(claims);
        assertEquals(email, claims.getSubject());
        assertEquals(userId, claims.get("userId", Long.class));
        assertEquals(role, claims.get("role", String.class));
        assertEquals("test-issuer", claims.getIssuer());
    }

    @Test
    void shouldThrowExceptionForInvalidToken() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When & Then
        assertThrows(Exception.class, () -> jwtService.validateToken(invalidToken));
    }

    @Test
    void shouldThrowExceptionForExpiredToken() throws InterruptedException {
        // Given
        ReflectionTestUtils.setField(jwtService, "jwtExpiry", 1L); // 1ms expiry
        String email = "test@example.com";
        Long userId = 123L;
        String role = "USER";
        String token = jwtService.generateToken(email, userId, role);

        // Wait for token to expire
        Thread.sleep(10);

        // When & Then
        assertThrows(Exception.class, () -> jwtService.validateToken(token));
    }
}