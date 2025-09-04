package co.com.bancolombia.security.config;

import co.com.bancolombia.usecase.portUtils.PasswordEncoderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BCryptPasswordEncoderAdapter implements PasswordEncoderPort {

    private static final String NULL_OR_EMPTY_PASSWORD_MESSAGE = "Password cannot be null or empty";

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encodePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException(NULL_OR_EMPTY_PASSWORD_MESSAGE);
        }
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}