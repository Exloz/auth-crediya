package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.RoleId;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase implements UserUseCasePort {

    private static final String ADMIN_EMAIL_DOMAIN = "@crediya.com";

    private final UserRepository userRepository;

    public Mono<User> createUser(User user) {
        checkEmailRole(user);
        return userRepository.validateEmailNotExists(user)
                .then(userRepository.saveUser(user));
    }

    private static void checkEmailRole(User user) {
        if (user.getEmail().contains(ADMIN_EMAIL_DOMAIN)) {
            user.setRoleId(RoleId.ADMIN);
        } else {
            user.setRoleId(RoleId.USER);
        }
    }
}
