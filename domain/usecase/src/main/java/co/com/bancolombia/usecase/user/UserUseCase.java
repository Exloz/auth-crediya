package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.RoleId;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase implements UserUseCasePort {

    private final UserRepository userRepository;

    public Mono<User> createUser(User user) {
        checkEmailRole(user);
        return userRepository.getByEmail(user)
                .then(userRepository.saveUser(user));
    }

    private static void checkEmailRole(User user) {
        if (user.getEmail().contains("@crediya.com")) {
            user.setRoleId(RoleId.ADMIN);
        } else {
            user.setRoleId(RoleId.USER);
        }
    }
}
