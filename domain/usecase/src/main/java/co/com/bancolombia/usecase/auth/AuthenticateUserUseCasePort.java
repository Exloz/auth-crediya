package co.com.bancolombia.usecase.auth;

import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Mono;

public interface AuthenticateUserUseCasePort {
    Mono<User> authenticate(String email, String password);
}

