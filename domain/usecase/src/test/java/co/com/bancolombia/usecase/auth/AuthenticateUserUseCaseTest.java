package co.com.bancolombia.usecase.auth;

import co.com.bancolombia.model.user.Credentials;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.exception.InvalidCredentialsException;
import co.com.bancolombia.model.user.gateways.CredentialsRepository;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.usecase.user.PasswordEncoderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CredentialsRepository credentialsRepository;

    private PasswordEncoder passwordEncoder;
    private PasswordEncoderPort passwordEncoderPort;
    private AuthenticateUserUseCase authenticateUserUseCase;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        passwordEncoderPort = new co.com.bancolombia.api.config.BCryptPasswordEncoderService(passwordEncoder);
        authenticateUserUseCase = new AuthenticateUserUseCase(
            userRepository,
            credentialsRepository,
            passwordEncoderPort
        );
    }

    @Test
    void shouldAuthenticateWithValidCredentials() {
        // Given
        String email = "test@example.com";
        String rawPassword = "testPassword123";
        String hashedPassword = passwordEncoderPort.encodePassword(rawPassword);

        User user = User.builder()
            .userId(1L)
            .email(email)
            .build();

        Credentials credentials = Credentials.builder()
            .userId(1L)
            .password(hashedPassword)
            .build();

        when(userRepository.findByEmail(email)).thenReturn(Mono.just(user));
        when(credentialsRepository.findByUserId(1L)).thenReturn(Mono.just(credentials));

        // When & Then
        StepVerifier.create(authenticateUserUseCase.authenticate(email, rawPassword))
            .expectNext(user)
            .verifyComplete();
    }

    @Test
    void shouldRejectInvalidPassword() {
        // Given
        String email = "test@example.com";
        String wrongPassword = "wrongPassword";
        String correctPassword = "correctPassword";
        String hashedPassword = passwordEncoderPort.encodePassword(correctPassword);

        User user = User.builder()
            .userId(1L)
            .email(email)
            .build();

        Credentials credentials = Credentials.builder()
            .userId(1L)
            .password(hashedPassword)
            .build();

        when(userRepository.findByEmail(email)).thenReturn(Mono.just(user));
        when(credentialsRepository.findByUserId(1L)).thenReturn(Mono.just(credentials));

        // When & Then
        StepVerifier.create(authenticateUserUseCase.authenticate(email, wrongPassword))
            .expectError(InvalidCredentialsException.class)
            .verify();
    }

    @Test
    void shouldRejectInvalidEmail() {
        // Given
        String invalidEmail = "nonexistent@example.com";
        String password = "testPassword";

        when(userRepository.findByEmail(invalidEmail)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(authenticateUserUseCase.authenticate(invalidEmail, password))
            .expectError(InvalidCredentialsException.class)
            .verify();
    }

    @Test
    void shouldRejectNullCredentials() {
        // When & Then
        StepVerifier.create(authenticateUserUseCase.authenticate(null, "password"))
            .expectError(InvalidCredentialsException.class)
            .verify();

        StepVerifier.create(authenticateUserUseCase.authenticate("email@example.com", null))
            .expectError(InvalidCredentialsException.class)
            .verify();
    }
}