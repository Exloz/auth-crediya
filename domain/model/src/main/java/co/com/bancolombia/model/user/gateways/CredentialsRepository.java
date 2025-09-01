package co.com.bancolombia.model.user.gateways;

import co.com.bancolombia.model.user.Credentials;
import reactor.core.publisher.Mono;

public interface CredentialsRepository {
    Mono<Boolean> existsByUserId(Long userId);
    Mono<Credentials> save(Credentials credentials);
    Mono<Credentials> findByUserId(Long userId);
}
