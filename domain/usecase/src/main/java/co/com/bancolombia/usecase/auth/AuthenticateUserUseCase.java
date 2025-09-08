package co.com.bancolombia.usecase.auth;

import co.com.bancolombia.model.user.Credentials;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.exception.InvalidCredentialsException;
import co.com.bancolombia.model.user.gateways.CredentialsRepository;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.usecase.portUtils.PasswordEncoderPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthenticateUserUseCase implements AuthenticateUserUseCasePort {

    private static final String INVALID_CREDENTIALS = "Invalid email or password";

    private final UserRepository userRepository;
    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoderPort passwordEncoder;

    @Override
    public Mono<User> authenticate(String email, String password) {
        String normalizedEmail = email == null ? null : email.trim();
        if (normalizedEmail == null || normalizedEmail.isEmpty() || password == null || password.isEmpty()) {
            return Mono.error(new InvalidCredentialsException(INVALID_CREDENTIALS));
        }

        return userRepository.findByEmail(normalizedEmail)
                .switchIfEmpty(Mono.error(new InvalidCredentialsException(INVALID_CREDENTIALS)))
                .flatMap(user -> credentialsRepository.findByUserId(user.getUserId())
                        .switchIfEmpty(Mono.error(new InvalidCredentialsException(INVALID_CREDENTIALS)))
                        .flatMap(creds -> verifyPassword(password, creds)
                                .thenReturn(user)
                        )
                );
    }

    private Mono<Void> verifyPassword(String rawPassword, Credentials creds) {
        boolean match = creds.getPassword() != null &&
                       passwordEncoder.matches(rawPassword, creds.getPassword());
        return match ? Mono.empty() : Mono.error(new InvalidCredentialsException(INVALID_CREDENTIALS));
    }
}

