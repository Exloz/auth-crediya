package co.com.bancolombia.api;

import co.com.bancolombia.api.config.GlobalExceptionHandler;
import co.com.bancolombia.api.config.OpenApiConfig;
import co.com.bancolombia.api.config.ValidationConfig;
import co.com.bancolombia.api.dto.register.UserRegisterReq;
import co.com.bancolombia.api.dto.register.UserRegisterRes;
import co.com.bancolombia.api.mapper.UserMapper;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.exception.UserAlreadyExistsException;
import co.com.bancolombia.usecase.user.UserUseCasePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, OpenApiConfig.class, ValidationConfig.class, GlobalExceptionHandler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserUseCasePort userUseCasePort;

    @MockitoBean
    private UserMapper userMapper;

    private UserRegisterReq validUserRequest;
    private UserRegisterRes userResponse;

    @BeforeEach
    void setUp() {
        validUserRequest = new UserRegisterReq(
            "Juan",
            "Pérez",
            LocalDate.of(1990, 1, 15),
            "Calle 123 #45-67",
            "12345678",
            "juan.perez@email.com",
            new BigDecimal("2500000.00"),
            "+57 300 123 4567",
            null // role omitted -> defaults to USER
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
    void shouldRegisterUserSuccessfully() throws Exception {
        // Given
        User domainUser = User.builder()
            .name("Juan")
            .lastName("Pérez")
            .email("juan.perez@email.com")
            .userId(1L)
            .build();
        Mockito.when(userMapper.toModel(any(UserRegisterReq.class)))
            .thenReturn(domainUser);
        Mockito.when(userUseCasePort.createUser(any()))
            .thenReturn(Mono.just(domainUser));
        Mockito.when(userMapper.toResponse(any()))
            .thenReturn(userResponse);

        // When & Then
        webTestClient.post()
            .uri("/api/v1/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(validUserRequest)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(UserRegisterRes.class)
            .value(response -> {
                assert response.userId().equals(1L);
                assert response.name().equals("Juan");
                assert response.lastName().equals("Pérez");
                assert response.email().equals("juan.perez@email.com");
            });
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        // Given - Invalid email format
        UserRegisterReq invalidRequest = new UserRegisterReq(
            "Juan",
            "Pérez",
            LocalDate.of(1990, 1, 15),
            "Calle 123 #45-67",
            "12345678",
            "invalid-email", // Invalid email
            new BigDecimal("2500000.00"),
            "+57 300 123 4567",
            null
        );

        // When & Then
        webTestClient.post()
            .uri("/api/v1/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequest)
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType("application/problem+json");
    }

    @Test
    void shouldReturnBadRequestWhenRequiredFieldsAreMissing() throws Exception {
        // Given - Missing required fields
        UserRegisterReq invalidRequest = new UserRegisterReq(
            "", // Empty name
            "", // Empty lastName
            null, // Null birthDate
            "Calle 123 #45-67",
            "12345678",
            "juan.perez@email.com",
            null, // Null baseSalary
            "+57 300 123 4567",
            null
        );

        // When & Then
        webTestClient.post()
            .uri("/api/v1/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequest)
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType("application/problem+json");
    }

    @Test
    void shouldReturnBadRequestWhenSalaryIsOutOfRange() {
        // Given - Salary too high
        UserRegisterReq invalidRequest = new UserRegisterReq(
            "Juan",
            "Pérez",
            LocalDate.of(1990, 1, 15),
            "Calle 123 #45-67",
            "12345678",
            "juan.perez@email.com",
            new BigDecimal("20000000.00"), // Salary too high (> 15,000,000)
            "+57 300 123 4567",
            null
        );

        // When & Then
        webTestClient.post()
            .uri("/api/v1/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequest)
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType("application/problem+json");
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        // Given
        User domainUser = User.builder()
            .name("Juan")
            .lastName("Pérez")
            .email("juan.perez@email.com")
            .userId(1L)
            .build();
        Mockito.when(userMapper.toModel(any(UserRegisterReq.class)))
            .thenReturn(domainUser);
        Mockito.when(userUseCasePort.createUser(any()))
            .thenReturn(Mono.error(new UserAlreadyExistsException("User with email juan.perez@email.com already exists")));

        // When & Then
        webTestClient.post()
            .uri("/api/v1/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(validUserRequest)
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectHeader().contentType("application/problem+json");
    }
}
