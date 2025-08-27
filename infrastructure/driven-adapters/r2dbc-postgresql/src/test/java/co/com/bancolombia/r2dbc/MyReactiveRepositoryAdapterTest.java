package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.exception.UserAlreadyExistsException;
import co.com.bancolombia.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyReactiveRepositoryAdapterTest {

    @InjectMocks
    MyReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    MyReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private User validUser;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        validUser = User.builder()
            .name("Juan")
            .lastName("Pérez")
            .email("juan.perez@email.com")
            .birthDate(LocalDate.of(1990, 1, 15))
            .address("Calle 123 #45-67")
            .idDocument("12345678")
            .baseSalary(new BigDecimal("2500000.00"))
            .phoneNumber("+57 300 123 4567")
            .build();

        userEntity = UserEntity.builder()
            .userId(1L)
            .name("Juan")
            .lastName("Pérez")
            .email("juan.perez@email.com")
            .birthDate(LocalDate.of(1990, 1, 15))
            .address("Calle 123 #45-67")
            .idDocument("12345678")
            .baseSalary(new BigDecimal("2500000.00"))
            .phoneNumber("+57 300 123 4567")
            .build();
    }

    @Test
    void shouldSaveUserSuccessfully() {
        // Given
        User savedUser = validUser.toBuilder().userId(1L).build();
        when(repository.save(any(UserEntity.class)))
            .thenReturn(Mono.just(userEntity));
        when(mapper.map(any(User.class), any()))
            .thenReturn(userEntity);
        when(mapper.map(any(UserEntity.class), any()))
            .thenReturn(savedUser);

        // When
        Mono<User> result = repositoryAdapter.saveUser(validUser);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(user -> {
                assert user.getUserId().equals(1L);
                assert user.getName().equals("Juan");
                assert user.getEmail().equals("juan.perez@email.com");
                return true;
            })
            .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenDataIntegrityViolationOccurs() {
        // Given
        DataIntegrityViolationException integrityException = new DataIntegrityViolationException("Duplicate key");

        when(repository.save(any(UserEntity.class)))
            .thenReturn(Mono.error(integrityException));
        when(mapper.map(any(User.class), any()))
            .thenReturn(userEntity);

        // When
        Mono<User> result = repositoryAdapter.saveUser(validUser);

        // Then
        StepVerifier.create(result)
            .expectError(UserAlreadyExistsException.class)
            .verify();
    }

    @Test
    void shouldValidateEmailNotExistsWhenEmailIsUnique() {
        // Given
        when(repository.findByEmail("juan.perez@email.com"))
            .thenReturn(Mono.empty());

        // When
        Mono<Void> result = repositoryAdapter.validateEmailNotExists(validUser);

        // Then
        StepVerifier.create(result)
            .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenEmailAlreadyExists() {
        // Given
        when(repository.findByEmail("juan.perez@email.com"))
            .thenReturn(Mono.just(userEntity));

        // When
        Mono<Void> result = repositoryAdapter.validateEmailNotExists(validUser);

        // Then
        StepVerifier.create(result)
            .expectError(UserAlreadyExistsException.class)
            .verify();
    }

    @Test
    void shouldHandleRepositoryErrors() {
        // Given
        RuntimeException repositoryError = new RuntimeException("Database connection error");

        when(repository.save(any(UserEntity.class)))
            .thenReturn(Mono.error(repositoryError));
        when(mapper.map(any(User.class), any()))
            .thenReturn(userEntity);

        // When
        Mono<User> result = repositoryAdapter.saveUser(validUser);

        // Then
        StepVerifier.create(result)
            .expectError(RuntimeException.class)
            .verify();
    }
}
