package co.com.bancolombia.api;

import co.com.bancolombia.usecase.auth.TokenServicePort;
import co.com.bancolombia.api.dto.register.UserRegisterReq;
import co.com.bancolombia.api.mapper.UserMapper;
import co.com.bancolombia.api.dto.login.LoginReq;
import co.com.bancolombia.api.dto.login.LoginRes;
import co.com.bancolombia.usecase.user.UserUseCasePort;
import co.com.bancolombia.usecase.auth.AuthenticateUserUseCasePort;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private static final String RECEIVED_USER_REGISTRATION_REQUEST = "Received user registration request: {}";
    private static final String USER_REGISTERED_SUCCESSFULLY = "User registered successfully";
    private static final String ERROR_REGISTERING_USER = "Error registering user: {}";
    private static final String RECEIVED_LOGIN_REQUEST = "Received login request for: {}";

    private static final String UNKNOWN_ORIGIN = "Unknown origin";

    private final UserUseCasePort useCase;
    private final UserMapper mapper;
    private final Validator validator;
    private final AuthenticateUserUseCasePort authenticateUserUseCase;
    private final TokenServicePort tokenServicePort;

    @PreAuthorize("hasRole('ADMIN') or hasRole('ASESOR')")
    public Mono<ServerResponse> listenCreateUser(ServerRequest request) {
        return request.bodyToMono(UserRegisterReq.class)
                .doOnNext(req -> log.info(RECEIVED_USER_REGISTRATION_REQUEST, req))
                .flatMap(this::validateRequest)
                .flatMap(req ->
                    useCase.createUser(mapper.toModel(req), req.password())
                            .map(mapper::toResponse)
                            .flatMap(userRes -> ServerResponse.status(HttpStatus.CREATED)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(userRes))
                )
                .doOnSuccess(response -> log.info(USER_REGISTERED_SUCCESSFULLY))
                .doOnError(error -> log.error(ERROR_REGISTERING_USER, getOriginOfError(error)));
    }


    private Mono<UserRegisterReq> validateRequest(UserRegisterReq request) {
        Set<ConstraintViolation<UserRegisterReq>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            return Mono.error(new ConstraintViolationException(violations));
        }
        return Mono.just(request);
    }


    private String getOriginOfError(Throwable error) {
        if (error.getStackTrace().length > 0) {
            var origin = error.getStackTrace()[0];
            return origin.getClassName() + "." + origin.getMethodName() + " (line " + origin.getLineNumber() + ")";
        }
        return UNKNOWN_ORIGIN;
    }

    public Mono<ServerResponse> listenLogin(ServerRequest request) {
        return request.bodyToMono(LoginReq.class)
                .doOnNext(req -> log.info(RECEIVED_LOGIN_REQUEST, req.email()))
                .flatMap(this::validateRequest)
                .flatMap(req -> authenticateUserUseCase.authenticate(req.email(), req.password()))
                .map(user -> {
                    String token = tokenServicePort.generateToken(user.getEmail(), user.getUserId(), user.getRoleId().name());
                    return new LoginRes(user.getUserId(), user.getEmail(), user.getRoleId(), token);
                })
                .flatMap(loginRes -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(loginRes)
                );
    }

    private Mono<LoginReq> validateRequest(LoginReq request) {
        Set<ConstraintViolation<LoginReq>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            return Mono.error(new ConstraintViolationException(violations));
        }
        return Mono.just(request);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ASESOR') or hasRole('USER')")
    public Mono<ServerResponse> listenGetUserById(ServerRequest request) {
        String userId = request.pathVariable("userId");
        return useCase.getUserById(userId)
                .map(mapper::toUserInfoResponse)
                .flatMap(userInfo -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userInfo))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
