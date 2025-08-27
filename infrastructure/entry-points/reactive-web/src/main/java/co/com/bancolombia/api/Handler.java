package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.UserRegisterReq;
import co.com.bancolombia.api.mapper.UserMapper;
import co.com.bancolombia.usecase.user.UserUseCasePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private final UserUseCasePort useCase;
    private final UserMapper mapper;
    private final Validator validator;

    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "El email ya está registrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ServerResponse> listenCreateUser(ServerRequest request) {
        log.info("Create user request received");
        return request.bodyToMono(UserRegisterReq.class)
                .doOnNext(this::validateRequest)
                .map(mapper::toModel)
                .flatMap(useCase::createUser)
                .map(mapper::toResponse)
                .flatMap(userRes -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userRes))
                .doOnError(error -> log.error("Error creating User: {}", error.getMessage()));
    }

    private void validateRequest(UserRegisterReq request) {
        Set<ConstraintViolation<UserRegisterReq>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            log.warn("Validation failed: {}", violations.size());
            throw new ConstraintViolationException(violations);
        }
        
    }
}
