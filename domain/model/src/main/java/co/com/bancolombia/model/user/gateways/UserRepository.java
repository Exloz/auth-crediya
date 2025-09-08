package co.com.bancolombia.model.user.gateways;

import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> saveUser(User user);
    Mono<Void> validateEmailNotExists(User user);
    Mono<User> findByEmail(String email);
    Mono<User> findByUserId(String idDocument);
}
