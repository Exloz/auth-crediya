package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.user.Credentials;
import co.com.bancolombia.model.user.gateways.CredentialsRepository;
import co.com.bancolombia.r2dbc.entity.CredentialsEntity;
import lombok.RequiredArgsConstructor;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CredentialsRepositoryAdapter implements CredentialsRepository {
    private final CredentialsReactiveRepository repository;
    private final ObjectMapper mapper;

    @Override
    public Mono<Boolean> existsByUserId(Long userId) {
        return repository.existsById(userId);
    }

    @Override
    public Mono<Credentials> save(Credentials credentials) {
        CredentialsEntity entity = mapper.map(credentials, CredentialsEntity.class);
        return repository.save(entity)
                .map(saved -> mapper.map(saved, Credentials.class));
    }

    @Override
    public Mono<Credentials> findByUserId(Long userId) {
        return repository.findById(userId)
                .map(entity -> mapper.map(entity, Credentials.class));
    }
}
