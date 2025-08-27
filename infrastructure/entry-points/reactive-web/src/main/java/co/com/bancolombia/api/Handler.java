package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.UserRegisterReq;
import co.com.bancolombia.api.mapper.UserMapper;
import co.com.bancolombia.usecase.user.UserUseCasePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolation;
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
        return request.bodyToMono(UserRegisterReq.class)
                .doOnNext(req -> log.info("Received user registration request: {}", req))
                .flatMap(this::validateRequest)
                .map(mapper::toModel)
                .flatMap(useCase::createUser)
                .map(mapper::toResponse)
                .flatMap(userRes -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userRes))
                .doOnSuccess(response -> log.info("User registered successfully"))
                .doOnError(error -> log.error("Error registering user: {}", getOriginOfError(error)));
    }

    private Mono<UserRegisterReq> validateRequest(UserRegisterReq request) {
        Set<ConstraintViolation<UserRegisterReq>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder("Validation errors: ");
            violations.forEach(violation -> message.append(violation.getMessage()).append("; "));
            return Mono.error(new IllegalArgumentException(message.toString()));
        }
        return Mono.just(request);
    }

    private String getOriginOfError(Throwable error) {
        if (error.getStackTrace().length > 0) {
            var origin = error.getStackTrace()[0];
            return origin.getClassName() + "." + origin.getMethodName() + " (line " + origin.getLineNumber() + ")";
        }
        return "Unknown origin";
    }
}
