package co.com.bancolombia.r2dbc;


import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.exception.UserAlreadyExistsException;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.r2dbc.entity.UserEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@Transactional
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<User, UserEntity, Long, MyReactiveRepository
        > implements UserRepository {
    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
    }

    @Override
    public Mono<User> saveUser(User user) {
        return super.save(user)
                .onErrorResume(DataIntegrityViolationException.class, ex -> {
                    log.error("User already exists: {}, {}", user.getName(), user.getIdDocument());
                    return Mono.error(new UserAlreadyExistsException("User with document number" + user.getIdDocument() + " already exists"));
                });
    }

    @Override
    public Mono<Void> getByEmail(User user) {
        log.debug("Checking if email exists in database: {}", user.getEmail());

        return repository.findByEmail(user.getEmail())
                .doOnNext(entity -> log.warn("Email already exists in database: {}", user.getEmail()))
                .flatMap(entity -> Mono.error(new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists")))
                .then();
    }
}
