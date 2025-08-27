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

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        String path = exchange.getRequest().getPath().value();
        HttpStatus status = determineHttpStatus(ex);
        log.error("Error on request {}: {}", path, ex.getMessage());

        ProblemDetail problem = toProblemDetail(ex, status, exchange);

        byte[] body;
        try {
            body = objectMapper.writeValueAsBytes(problem);
        } catch (Exception e) {
            log.error("Error serialization: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            byte[] fallback = "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Serialization error\"}".getBytes();
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(fallback)));
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.parseMediaType("application/problem+json"));
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
            case ConstraintViolationException e -> "Validation failed";
            case IllegalArgumentException e -> e.getMessage();
            case WebExchangeBindException e -> "Validation failed";
            default -> "An unexpected error occurred. Please try again later.";
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
        error.put("error", "Bad Request");
        error.put("message", message);
        error.put("status", HttpStatus.BAD_REQUEST.value());

        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(error);
    }

    private static Mono<ServerResponse> handleValidationException(WebExchangeBindException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("error", "Validation Error");
        errors.put("status", HttpStatus.BAD_REQUEST.value());

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getFieldErrors().forEach(error ->
            fieldErrors.put(error.getField(), error.getDefaultMessage()));

        errors.put("fieldErrors", fieldErrors);

        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errors);
    }

    private static Mono<ServerResponse> handleConflict(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Conflict");
        error.put("message", message);
        error.put("status", HttpStatus.CONFLICT.value());

        return ServerResponse.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(error);
    }

    private static Mono<ServerResponse> handleInternalServerError(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Internal Server Error");
        error.put("message", "An unexpected error occurred");
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(error);
    }
}
