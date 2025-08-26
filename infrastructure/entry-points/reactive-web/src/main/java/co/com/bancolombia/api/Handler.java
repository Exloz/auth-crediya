package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.UserRegisterReq;
import co.com.bancolombia.api.mapper.UserMapper;
import co.com.bancolombia.usecase.user.UserUseCasePort;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCasePort useCase;
    private final UserMapper mapper;
    private final Validator validator;

    public Mono<ServerResponse> listenCreateUser(ServerRequest request) {
        return request.bodyToMono(UserRegisterReq.class)
                .doOnNext(this::validateRequest)
                .map(mapper::toModel)
                .flatMap(useCase::createUser)
                .map(mapper::toResponse)
                .flatMap(userRes -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userRes))
                .onErrorResume(ConstraintViolationException.class, e ->
                    ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(createValidationErrorResponse(e)))
                .onErrorResume(Exception.class, e ->
                    ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", "Bad Request", "message", e.getMessage())));
    }

    private void validateRequest(UserRegisterReq request) {
        Set<ConstraintViolation<UserRegisterReq>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private Map<String, Object> createValidationErrorResponse(ConstraintViolationException e) {
        return Map.of(
            "error", "Validation Error",
            "message", "Request contains validation errors",
            "violations", e.getConstraintViolations().stream()
                .map(violation -> Map.of(
                    "field", violation.getPropertyPath().toString(),
                    "message", violation.getMessage()
                ))
                .collect(Collectors.toList())
        );
    }
}
