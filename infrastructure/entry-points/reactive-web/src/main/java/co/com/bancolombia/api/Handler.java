package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.UserRegisterReq;
import co.com.bancolombia.api.mapper.UserMapper;
import co.com.bancolombia.model.user.exception.UserAlreadyExistsException;
import co.com.bancolombia.usecase.user.UserUseCasePort;
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

    private static final String API_PATH = "/api/v1/usuarios";
    private static final String CONFLICT_ERROR_FORMAT = """
    {
      "status": 409,
      "error": "Conflict",
      "message": "%s",
      "path": "%s"
    }
    """;

    private static final String BAD_REQUEST_ERROR_FORMAT = """
    {
      "status": 400,
      "error": "Bad Request",
      "message": "Validation failed: %s",
      "path": "%s"
    }
    """;

    private static final String INTERNAL_SERVER_ERROR_FORMAT = """
    {
      "status": 500,
      "error": "Internal Server Error",
      "message": "An unexpected error occurred",
      "path": "%s"
    }
    """;


    private final UserUseCasePort useCase;
    private final UserMapper mapper;
    private final Validator validator;

    public Mono<ServerResponse> listenCreateUser(ServerRequest request) {
        log.info("Processing user registration request from {}", request.remoteAddress());

        return request.bodyToMono(UserRegisterReq.class)
                .doOnNext(this::validateRequest)
                .doOnNext(req -> log.debug("Validated request for user: {}", req.email()))
                .map(mapper::toModel)
                .flatMap(useCase::createUser)
                .map(mapper::toResponse)
                .flatMap(userRes -> {
                    log.info("User registered successfully with ID: {}", userRes.userId());
                    return ServerResponse.status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(userRes);
                })
                .onErrorResume(this::handleException);
    }

    private void validateRequest(UserRegisterReq request) {
        Set<ConstraintViolation<UserRegisterReq>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            log.warn("Validation failed for request: {}", violations.size());
            throw new ConstraintViolationException(violations);
        }
    }

    private Mono<ServerResponse> handleException(Throwable ex) {
        log.error("Handling exception in Handler: {} - {}", ex.getClass().getSimpleName(), ex.getMessage());

        if (ex instanceof UserAlreadyExistsException) {
            return ServerResponse.status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(String.format(CONFLICT_ERROR_FORMAT, ex.getMessage(), API_PATH));
        } else if (ex instanceof ConstraintViolationException) {
            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(String.format(BAD_REQUEST_ERROR_FORMAT, ex.getMessage(), API_PATH));
        } else {
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(String.format(INTERNAL_SERVER_ERROR_FORMAT, API_PATH));
        }
    }
}
