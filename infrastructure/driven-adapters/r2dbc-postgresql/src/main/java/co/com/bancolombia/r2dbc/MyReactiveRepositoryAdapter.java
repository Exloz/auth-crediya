package co.com.bancolombia.r2dbc;


import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.exception.UserAlreadyExistsException;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.r2dbc.entity.UserEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<User, UserEntity, Long, MyReactiveRepository
        > implements UserRepository {
    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
    }

    @Override
    public Mono<User> saveUser(User user) {
        return super.save(user)
                .onErrorResume(DataIntegrityViolationException.class, ex ->
                        Mono.error(new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists")));
    }

    @Override
    public Mono<Void> getByEmail(User user) {
        return repository.findByEmail(user.getEmail())
                .flatMap(entity -> Mono.error(new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists")))
                .then();
    }
}
