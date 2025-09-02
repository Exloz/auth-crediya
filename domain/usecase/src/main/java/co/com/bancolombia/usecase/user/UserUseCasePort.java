package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Mono;

public interface UserUseCasePort {
    Mono<User> createUser(User user, String rawPassword);
}
