package co.com.bancolombia.api.config;

import co.com.bancolombia.model.user.exception.UserAlreadyExistsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler implements WebExceptionHandler {

    // Log messages
    private static final String ERROR_ON_REQUEST_LOG = "Error on request {}: {}";
    private static final String ERROR_SERIALIZATION_LOG = "Error serialization: {}";

    // Error messages
    private static final String VALIDATION_FAILED = "Validation failed";
    private static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred. Please try again later.";
    private static final String SERIALIZATION_ERROR_MESSAGE = "Serialization error";
    private static final String UNEXPECTED_ERROR_SHORT = "An unexpected error occurred";

    // Error response fields
    private static final String ERROR_FIELD = "error";
    private static final String MESSAGE_FIELD = "message";
    private static final String STATUS_FIELD = "status";
    private static final String FIELD_ERRORS_FIELD = "fieldErrors";

    // Error types
    private static final String BAD_REQUEST_ERROR = "Bad Request";
    private static final String VALIDATION_ERROR = "Validation Error";
    private static final String CONFLICT_ERROR = "Conflict";
    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

    // Content types
    private static final String PROBLEM_JSON_CONTENT_TYPE = "application/problem+json";

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        String path = exchange.getRequest().getPath().value();
        HttpStatus status = determineHttpStatus(ex);
        log.error(ERROR_ON_REQUEST_LOG, path, ex.getMessage());

        ProblemDetail problem = toProblemDetail(ex, status, exchange);

        byte[] body;
        try {
            body = objectMapper.writeValueAsBytes(problem);
        } catch (Exception e) {
            log.error(ERROR_SERIALIZATION_LOG, e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            byte[] fallback = ("{\"" + STATUS_FIELD + "\":500,\"" + ERROR_FIELD + "\":\"" + INTERNAL_SERVER_ERROR + "\",\"" + MESSAGE_FIELD + "\":\"" + SERIALIZATION_ERROR_MESSAGE + "\"}").getBytes();
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(fallback)));
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.parseMediaType(PROBLEM_JSON_CONTENT_TYPE));
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body)));
    }

    private HttpStatus determineHttpStatus(Throwable ex) {
        return switch (ex) {
            case UserAlreadyExistsException ignored -> HttpStatus.CONFLICT;
            case ConstraintViolationException ignored -> HttpStatus.BAD_REQUEST;
            case IllegalArgumentException ignored -> HttpStatus.BAD_REQUEST;
            case WebExchangeBindException ignored -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private ProblemDetail toProblemDetail(Throwable ex, HttpStatus status, ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();

        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle(status.getReasonPhrase());
        pd.setDetail(determineErrorMessage(ex));
        pd.setProperty("path", path);
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        pd.setProperty("traceId", exchange.getRequest().getId());

        if (ex instanceof WebExchangeBindException webExchangeBindException) {
            List<Map<String, String>> violations = webExchangeBindException.getFieldErrors().stream()
                    .map(error -> Map.of(
                            "field", error.getField(),
                            "message", error.getDefaultMessage()
                    ))
                    .toList();
            pd.setProperty("violations", violations);
        } else if (ex instanceof ConstraintViolationException cve) {
            List<Map<String, String>> violations = cve.getConstraintViolations().stream()
                    .map(v -> Map.of(
                            "field", v.getPropertyPath().toString(),
                            "message", v.getMessage()
                    ))
                    .toList();
            pd.setProperty("violations", violations);
        }

        return pd;
    }

    private String determineErrorMessage(Throwable ex) {
        return switch (ex) {
            case UserAlreadyExistsException e -> e.getMessage();
            case ConstraintViolationException e -> VALIDATION_FAILED;
            case IllegalArgumentException e -> e.getMessage();
            case WebExchangeBindException e -> VALIDATION_FAILED;
            default -> UNEXPECTED_ERROR_MESSAGE;
        };
    }

    public static Mono<ServerResponse> handleException(Throwable throwable) {
        return switch (throwable) {
            case UserAlreadyExistsException ignored -> handleConflict(throwable.getMessage());
            case ConstraintViolationException ignored -> handleBadRequest(throwable.getMessage());
            case IllegalArgumentException ignored -> handleBadRequest(throwable.getMessage());
            case WebExchangeBindException webExchangeBindException ->
                    handleValidationException(webExchangeBindException);
            default -> handleInternalServerError(throwable.getMessage());
        };
    }

    private static Mono<ServerResponse> handleBadRequest(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put(ERROR_FIELD, BAD_REQUEST_ERROR);
        error.put(MESSAGE_FIELD, message);
        error.put(STATUS_FIELD, HttpStatus.BAD_REQUEST.value());

        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(error);
    }

    private static Mono<ServerResponse> handleValidationException(WebExchangeBindException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put(ERROR_FIELD, VALIDATION_ERROR);
        errors.put(STATUS_FIELD, HttpStatus.BAD_REQUEST.value());

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getFieldErrors().forEach(error ->
            fieldErrors.put(error.getField(), error.getDefaultMessage()));

        errors.put(FIELD_ERRORS_FIELD, fieldErrors);

        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errors);
    }

    private static Mono<ServerResponse> handleConflict(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put(ERROR_FIELD, CONFLICT_ERROR);
        error.put(MESSAGE_FIELD, message);
        error.put(STATUS_FIELD, HttpStatus.CONFLICT.value());

        return ServerResponse.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(error);
    }

    private static Mono<ServerResponse> handleInternalServerError(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put(ERROR_FIELD, INTERNAL_SERVER_ERROR);
        error.put(MESSAGE_FIELD, UNEXPECTED_ERROR_SHORT);
        error.put(STATUS_FIELD, HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(error);
    }
}
