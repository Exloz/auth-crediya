package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.entity.CredentialsEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CredentialsReactiveRepository extends ReactiveCrudRepository<CredentialsEntity, Long> {

    @Query("INSERT INTO credentials (user_id, password) VALUES (:userId, :password) ON CONFLICT (user_id) DO UPDATE SET password = :password")
    Mono<Void> upsertCredentials(Long userId, String password);
}

