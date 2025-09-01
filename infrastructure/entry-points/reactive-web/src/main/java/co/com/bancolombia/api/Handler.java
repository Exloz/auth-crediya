package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.UserRegisterReq;
import co.com.bancolombia.api.dto.AdminUserRegisterReq;
import co.com.bancolombia.api.mapper.UserMapper;
import co.com.bancolombia.model.user.RoleId;
import co.com.bancolombia.usecase.user.UserUseCasePort;
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

    // Log messages
    private static final String RECEIVED_USER_REGISTRATION_REQUEST = "Received user registration request: {}";
    private static final String USER_REGISTERED_SUCCESSFULLY = "User registered successfully";
    private static final String ERROR_REGISTERING_USER = "Error registering user: {}";
    private static final String RECEIVED_PRIVILEGED_REGISTRATION_REQUEST = "Received privileged user registration request: {}";

    // Validation messages
    private static final String VALIDATION_ERRORS_PREFIX = "Validation errors: ";
    private static final String VALIDATION_ERROR_SEPARATOR = "; ";
    private static final String UNKNOWN_ORIGIN = "Unknown origin";

    private final UserUseCasePort useCase;
    private final UserMapper mapper;
    private final Validator validator;

    public Mono<ServerResponse> listenCreateUser(ServerRequest request) {
        return request.bodyToMono(UserRegisterReq.class)
                .doOnNext(req -> log.info(RECEIVED_USER_REGISTRATION_REQUEST, req))
                .flatMap(this::validateRequest)
                .map(mapper::toModel)
                .flatMap(useCase::createUser)
                .map(mapper::toResponse)
                .flatMap(userRes -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userRes))
                .doOnSuccess(response -> log.info(USER_REGISTERED_SUCCESSFULLY))
                .doOnError(error -> log.error(ERROR_REGISTERING_USER, getOriginOfError(error)));
    }

    public Mono<ServerResponse> listenRegisterPrivilegedUser(ServerRequest request) {
        return request.bodyToMono(AdminUserRegisterReq.class)
                .doOnNext(req -> log.info(RECEIVED_PRIVILEGED_REGISTRATION_REQUEST, req))
                .flatMap(this::validateRequest)
                .map(req -> {
                    //TODO: Revisar que esto se pueda validar en otra parte
                    var user = mapper.toModel(req);
                    var roleStr = req.role().toUpperCase();
                    if ("ADMIN".equals(roleStr)) {
                        user.setRoleId(RoleId.ADMIN);
                    } else if ("ASESOR".equals(roleStr)) {
                        user.setRoleId(RoleId.ASESOR);
                    } else {
                        throw new IllegalArgumentException("Invalid role: " + roleStr);
                    }
                    return user;
                })
                .flatMap(useCase::createUser)
                .map(mapper::toResponse)
                .flatMap(userRes -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userRes))
                .doOnSuccess(response -> log.info(USER_REGISTERED_SUCCESSFULLY))
                .doOnError(error -> log.error(ERROR_REGISTERING_USER, getOriginOfError(error)));
    }

    private Mono<UserRegisterReq> validateRequest(UserRegisterReq request) {
        Set<ConstraintViolation<UserRegisterReq>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder(VALIDATION_ERRORS_PREFIX);
            violations.forEach(violation -> message.append(violation.getMessage()).append(VALIDATION_ERROR_SEPARATOR));
            return Mono.error(new IllegalArgumentException(message.toString()));
        }
        return Mono.just(request);
    }

    private Mono<AdminUserRegisterReq> validateRequest(AdminUserRegisterReq request) {
        Set<ConstraintViolation<AdminUserRegisterReq>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder(VALIDATION_ERRORS_PREFIX);
            violations.forEach(violation -> message.append(violation.getMessage()).append(VALIDATION_ERROR_SEPARATOR));
            return Mono.error(new IllegalArgumentException(message.toString()));
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
}
