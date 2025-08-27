package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.RoleId;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.exception.UserAlreadyExistsException;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    private UserUseCase userUseCase;

    private UserRepository userRepository;

    private User validUser;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class, Mockito.RETURNS_DEEP_STUBS);
        userUseCase = new UserUseCase(userRepository);

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
    }

    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        User savedUser = validUser.toBuilder()
            .userId(1L)
            .roleId(RoleId.USER)
            .build();

        when(userRepository.validateEmailNotExists(validUser))
            .thenReturn(Mono.empty());
        when(userRepository.saveUser(any(User.class)))
            .thenReturn(Mono.just(savedUser));

        // When
        Mono<User> result = userUseCase.createUser(validUser);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(user -> {
                assert user.getUserId().equals(1L);
                assert user.getName().equals("Juan");
                assert user.getLastName().equals("Pérez");
                assert user.getEmail().equals("juan.perez@email.com");
                assert user.getRoleId().equals(RoleId.USER); // Email no contiene @crediya.com
                return true;
            })
            .verifyComplete();
    }

    @Test
    void shouldAssignAdminRoleForCrediyaEmail() {
        // Given
        User adminUser = validUser.toBuilder()
            .email("admin@crediya.com")
            .build();
        User savedUser = adminUser.toBuilder()
            .userId(1L)
            .roleId(RoleId.ADMIN)
            .build();

        when(userRepository.validateEmailNotExists(adminUser))
            .thenReturn(Mono.empty());
        when(userRepository.saveUser(any(User.class)))
            .thenReturn(Mono.just(savedUser));

        // When
        Mono<User> result = userUseCase.createUser(adminUser);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(user -> {
                assert user.getRoleId().equals(RoleId.ADMIN);
                return true;
            })
            .verifyComplete();
    }

    @Test
    void shouldAssignUserRoleForRegularEmail() {
        // Given
        when(userRepository.validateEmailNotExists(validUser))
            .thenReturn(Mono.empty());
        when(userRepository.saveUser(any(User.class)))
            .thenReturn(Mono.just(validUser.toBuilder().userId(1L).roleId(RoleId.USER).build()));

        // When
        Mono<User> result = userUseCase.createUser(validUser);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(user -> {
                assert user.getRoleId().equals(RoleId.USER);
                return true;
            })
            .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenEmailAlreadyExists() {
        // Given
        UserAlreadyExistsException exception = new UserAlreadyExistsException("Email already exists");

        // Mock the repository to return error on validateEmailNotExists
        when(userRepository.validateEmailNotExists(any(User.class)))
            .thenReturn(Mono.error(exception));
        when(userRepository.saveUser(any(User.class)))
            .thenReturn(Mono.just(validUser)); // This shouldn't be called but just in case

        // When
        Mono<User> result = userUseCase.createUser(validUser);

        // Then
        StepVerifier.create(result)
            .expectError(UserAlreadyExistsException.class)
            .verify();
    }

    @Test
    void shouldReturnErrorWhenSaveFails() {
        // Given
        RuntimeException saveException = new RuntimeException("Database error");

        when(userRepository.validateEmailNotExists(validUser))
            .thenReturn(Mono.empty());
        when(userRepository.saveUser(any(User.class)))
            .thenReturn(Mono.error(saveException));

        // When
        Mono<User> result = userUseCase.createUser(validUser);

        // Then
        StepVerifier.create(result)
            .expectError(RuntimeException.class)
            .verify();
    }
}