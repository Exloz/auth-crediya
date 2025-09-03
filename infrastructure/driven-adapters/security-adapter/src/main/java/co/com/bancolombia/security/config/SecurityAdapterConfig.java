package co.com.bancolombia.security.config;

import co.com.bancolombia.usecase.auth.TokenServicePort;
import co.com.bancolombia.usecase.user.PasswordEncoderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityAdapterConfig {
}