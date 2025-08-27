package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.UserRegisterReq;
import co.com.bancolombia.api.dto.UserRegisterRes;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.user.UserUseCasePort;
import co.com.bancolombia.api.mapper.UserMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    private Handler handler;

    @Mock
    private UserUseCasePort userUseCasePort;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Validator validator;

    private UserRegisterReq validUserRequest;
    private UserRegisterRes userResponse;

    @BeforeEach
    void setUp() {
        handler = new Handler(userUseCasePort, userMapper, validator);

        validUserRequest = new UserRegisterReq(
            "Juan",
            "Pérez",
            LocalDate.of(1990, 1, 15),
            "Calle 123 #45-67",
            "12345678",
            "juan.perez@email.com",
            new BigDecimal("2500000.00"),
            "+57 300 123 4567"
        );

        userResponse = new UserRegisterRes(
            1L,
            "Juan",
            "Pérez",
            LocalDate.of(1990, 1, 15),
            "Calle 123 #45-67",
            "juan.perez@email.com",
            new BigDecimal("2500000.00"),
            "+57 300 123 4567"
        );
    }

    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        ServerRequest request = Mockito.mock(ServerRequest.class);
        User domainUser = User.builder()
            .name("Juan")
            .lastName("Pérez")
            .email("juan.perez@email.com")
            .build();

        when(request.bodyToMono(UserRegisterReq.class))
            .thenReturn(Mono.just(validUserRequest));
        when(validator.validate(validUserRequest)).thenReturn(Set.of());
        when(userMapper.toModel(validUserRequest)).thenReturn(domainUser);
        when(userUseCasePort.createUser(domainUser)).thenReturn(Mono.just(domainUser));
        when(userMapper.toResponse(domainUser)).thenReturn(userResponse);

        // When
        Mono<ServerResponse> responseMono = handler.listenCreateUser(request);

        // Then
        StepVerifier.create(responseMono)
            .expectNextMatches(response ->
                response.statusCode().equals(HttpStatus.CREATED) &&
                response.headers().getContentType().equals(MediaType.APPLICATION_JSON))
            .verifyComplete();
    }

    @Test
    void shouldHandleValidationErrors() {
        // Given
        ServerRequest request = mock(ServerRequest.class);
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        @SuppressWarnings("unchecked")
        Set<ConstraintViolation<UserRegisterReq>> violations = (Set<ConstraintViolation<UserRegisterReq>>) (Set<?>) Set.of(violation);

        when(request.bodyToMono(UserRegisterReq.class))
            .thenReturn(Mono.just(validUserRequest));
        when(validator.validate(validUserRequest))
            .thenReturn(violations);

        // When
        Mono<ServerResponse> responseMono = handler.listenCreateUser(request);

        // Then
        StepVerifier.create(responseMono)
            .expectError(ConstraintViolationException.class)
            .verify();
    }

    @Test
    void shouldHandleBusinessLogicErrors() {
        // Given
        ServerRequest request = mock(ServerRequest.class);
        RuntimeException businessException = new RuntimeException("Email already exists");
        User domainUser = User.builder().build();

        when(request.bodyToMono(UserRegisterReq.class))
            .thenReturn(Mono.just(validUserRequest));
        when(validator.validate(validUserRequest)).thenReturn(Set.of());
        when(userMapper.toModel(validUserRequest)).thenReturn(domainUser);
        when(userUseCasePort.createUser(domainUser))
            .thenReturn(Mono.error(businessException));

        // When
        Mono<ServerResponse> responseMono = handler.listenCreateUser(request);

        // Then
        StepVerifier.create(responseMono)
            .expectError(RuntimeException.class)
            .verify();
    }

    @Test
    void shouldValidateRequestWithValidData() {
        // Given
        Set<ConstraintViolation<UserRegisterReq>> violations = Set.of();

        // When
        // The validateRequest method is private, so we test it indirectly
        // by ensuring no exception is thrown in the main flow

        // Then
        // If validation passes, no ConstraintViolationException should be thrown
        // This is tested implicitly in the successful creation test
    }

    @Test
    void shouldValidateRequestWithInvalidData() {
        // Given
        // When & Then
        // The private validateRequest method should throw ConstraintViolationException
        // This is tested in the validation error test above
    }
}