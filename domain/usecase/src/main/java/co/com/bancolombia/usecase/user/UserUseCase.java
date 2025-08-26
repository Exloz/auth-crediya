package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase implements UserUseCasePort {

    private final UserRepository userRepository;

    public Mono<User> createUser(User user) {
        return userRepository.getByEmail(user)
                .flatMap(x -> userRepository.saveUser(user));
    }
}
