package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.Credentials;
import co.com.bancolombia.model.user.RoleId;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.CredentialsRepository;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase implements UserUseCasePort {
    private final UserRepository userRepository;
    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoderPort passwordEncoder;

    public Mono<User> createUser(User user, String rawPassword) {
        if (user.getRoleId() == null) {
            user.setRoleId(inferRoleFromBusinessRules(user));
        }

        return userRepository.validateEmailNotExists(user)
                .then(userRepository.saveUser(user))
                .flatMap(savedUser -> {
                    String hashedPassword = passwordEncoder.encodePassword(rawPassword);
                    Credentials credentials = Credentials.builder()
                            .userId(savedUser.getUserId())
                            .password(hashedPassword)
                            .build();
                    return credentialsRepository.save(credentials)
                            .thenReturn(savedUser);
                });
    }

    private RoleId inferRoleFromBusinessRules(User user) {
        String email = user.getEmail();
        if (user.getRoleId() == RoleId.ADMIN && email != null && email.toLowerCase().endsWith("@crediya.com")) {
            return RoleId.ADMIN;
        }
        if (user.getRoleId() == RoleId.ASESOR && email != null && email.toLowerCase().endsWith("@crediya.com")) {
            return RoleId.ASESOR;
        }
        return RoleId.USER;
    }
}
