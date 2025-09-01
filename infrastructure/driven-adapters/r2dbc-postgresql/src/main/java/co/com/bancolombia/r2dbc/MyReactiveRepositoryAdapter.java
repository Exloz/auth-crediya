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
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<User, UserEntity, Long, MyReactiveRepository
        > implements UserRepository {

    // Log messages
    private static final String USER_ALREADY_EXISTS_LOG = "User already exists: {}, {}";
    private static final String VALIDATING_EMAIL_NOT_EXISTS = "Validating email does not exist in database: {}";
    private static final String EMAIL_ALREADY_EXISTS_IN_DB = "Email already exists in database: {}";

    // Exception messages
    private static final String USER_WITH_DOCUMENT_EXISTS = "User with document number";
    private static final String ALREADY_EXISTS_SUFFIX = " already exists";
    private static final String USER_WITH_EMAIL_EXISTS = "User with email ";

    private final TransactionalOperator transactionalOperator;

    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper, ReactiveTransactionManager transactionManager) {
        super(repository, mapper, d -> mapper.map(d, User.class));
        this.transactionalOperator = TransactionalOperator.create(transactionManager);
    }

    @Override
    public Mono<User> saveUser(User user) {
        return super.save(user)
                .onErrorMap(DataIntegrityViolationException.class, ex ->
                        new UserAlreadyExistsException(
                                USER_WITH_DOCUMENT_EXISTS + " " + user.getIdDocument() + ALREADY_EXISTS_SUFFIX
                        )
                )
                .as(transactionalOperator::transactional);
    }


    @Override
    public Mono<Void> validateEmailNotExists(User user) {
        log.debug(VALIDATING_EMAIL_NOT_EXISTS, user.getEmail());

        return repository.findByEmail(user.getEmail())
                .doOnNext(entity -> log.warn(EMAIL_ALREADY_EXISTS_IN_DB, user.getEmail()))
                .flatMap(entity -> Mono.error(new UserAlreadyExistsException(USER_WITH_EMAIL_EXISTS + user.getEmail() + ALREADY_EXISTS_SUFFIX)))
                .then();
    }
}
