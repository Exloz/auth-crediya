package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.UserRegisterReq;
import co.com.bancolombia.api.mapper.UserMapper;
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
                });
    }

    private void validateRequest(UserRegisterReq request) {
        Set<ConstraintViolation<UserRegisterReq>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            log.warn("Validation failed for request: {}", violations.size());
            throw new ConstraintViolationException(violations);
        }
    }
}
