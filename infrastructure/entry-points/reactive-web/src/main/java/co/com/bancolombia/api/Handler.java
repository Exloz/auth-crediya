package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.UserRegisterReq;
import co.com.bancolombia.api.dto.ValidationMessages;
import co.com.bancolombia.api.mapper.UserMapper;
import co.com.bancolombia.api.dto.LoginReq;
import co.com.bancolombia.api.dto.LoginRes;
import co.com.bancolombia.model.user.RoleId;
import co.com.bancolombia.usecase.user.UserUseCasePort;
import co.com.bancolombia.usecase.auth.AuthenticateUserUseCasePort;
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
    private static final String RECEIVED_LOGIN_REQUEST = "Received login request for: {}";

    // Validation messages
    private static final String VALIDATION_ERRORS_PREFIX = "Validation errors: ";
    private static final String VALIDATION_ERROR_SEPARATOR = "; ";
    private static final String UNKNOWN_ORIGIN = "Unknown origin";

    private final UserUseCasePort useCase;
    private final UserMapper mapper;
    private final Validator validator;
    private final AuthenticateUserUseCasePort authenticateUserUseCase;

    public Mono<ServerResponse> listenCreateUser(ServerRequest request) {
        return request.bodyToMono(UserRegisterReq.class)
                .doOnNext(req -> log.info(RECEIVED_USER_REGISTRATION_REQUEST, req))
                .flatMap(this::validateRequest)
                .map(req -> {
                    var user = mapper.toModel(req);
                    String roleStr = req.role() == null || req.role().isBlank() ? "USER" : req.role().trim().toUpperCase();
                    switch (roleStr) {
                        case "USER" -> user.setRoleId(RoleId.USER);
                        case "ADMIN" -> user.setRoleId(RoleId.ADMIN);
                        case "ASESOR" -> user.setRoleId(RoleId.ASESOR);
                        default -> throw new IllegalArgumentException("Invalid role: " + roleStr);
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


    //TODO: Refactor to reduce complexity
    private Mono<UserRegisterReq> validateRequest(UserRegisterReq request) {
        Set<ConstraintViolation<UserRegisterReq>> violations = validator.validate(request);
        StringBuilder message = new StringBuilder();
        if (!violations.isEmpty()) {
            message.append(VALIDATION_ERRORS_PREFIX);
            violations.forEach(violation -> message.append(violation.getMessage()).append(VALIDATION_ERROR_SEPARATOR));
        }

        String roleStr = (request.role() == null || request.role().isBlank()) ? "USER" : request.role().trim().toUpperCase();
        if (!("USER".equals(roleStr) || "ADMIN".equals(roleStr) || "ASESOR".equals(roleStr))) {
            message.append("Invalid role: ").append(roleStr).append(VALIDATION_ERROR_SEPARATOR);
        } else if ("USER".equals(roleStr)) {
            if (request.birthDate() == null) {
                message.append(ValidationMessages.BIRTH_DATE_REQUIRED).append(VALIDATION_ERROR_SEPARATOR);
            }
            if (request.baseSalary() == null) {
                message.append(ValidationMessages.BASE_SALARY_REQUIRED).append(VALIDATION_ERROR_SEPARATOR);
            }
        } else {
            if (request.idDocument() == null || request.idDocument().isBlank()) {
                message.append(ValidationMessages.ID_DOCUMENT_REQUIRED).append(VALIDATION_ERROR_SEPARATOR);
            }
            if (request.phoneNumber() == null || request.phoneNumber().isBlank()) {
                message.append(ValidationMessages.PHONE_REQUIRED).append(VALIDATION_ERROR_SEPARATOR);
            }
        }

        if (message.length() > 0) {
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

    public Mono<ServerResponse> listenLogin(ServerRequest request) {
        return request.bodyToMono(LoginReq.class)
                .doOnNext(req -> log.info(RECEIVED_LOGIN_REQUEST, req.email()))
                .flatMap(this::validateRequest)
                .flatMap(req -> authenticateUserUseCase.authenticate(req.email(), req.password()))
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new LoginRes(user.getUserId(), user.getEmail(), user.getRoleId()))
                );
    }

    private Mono<LoginReq> validateRequest(LoginReq request) {
        Set<ConstraintViolation<LoginReq>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder(VALIDATION_ERRORS_PREFIX);
            violations.forEach(violation -> message.append(violation.getMessage()).append(VALIDATION_ERROR_SEPARATOR));
            return Mono.error(new IllegalArgumentException(message.toString()));
        }
        return Mono.just(request);
    }
}
