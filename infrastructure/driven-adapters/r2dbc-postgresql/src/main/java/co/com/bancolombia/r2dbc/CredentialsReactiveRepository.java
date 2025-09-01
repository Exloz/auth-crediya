package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.entity.CredentialsEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CredentialsReactiveRepository extends ReactiveCrudRepository<CredentialsEntity, Long> {
}

