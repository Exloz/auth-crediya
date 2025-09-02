package co.com.bancolombia.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@ComponentScan(basePackages = "co.com.bancolombia.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

    private final PasswordEncoder passwordEncoder;

    @Bean
    public co.com.bancolombia.usecase.user.PasswordEncoderPort passwordEncoderPort() {
        return new co.com.bancolombia.api.config.BCryptPasswordEncoderService(passwordEncoder);
    }
}
