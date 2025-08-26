package co.com.bancolombia.api.config;

import co.com.bancolombia.model.user.exception.UserAlreadyExistsException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("GlobalExceptionHandler invoked for: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);

        HttpStatus status = determineHttpStatus(ex);
        String errorResponse = createErrorResponse(ex, exchange, status);

        return writeErrorResponse(exchange, errorResponse, status);
    }

    private HttpStatus determineHttpStatus(Throwable ex) {
        return switch (ex) {
            case UserAlreadyExistsException ignored -> HttpStatus.CONFLICT;
            case ConstraintViolationException ignored -> HttpStatus.BAD_REQUEST;
            case IllegalArgumentException ignored -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private String createErrorResponse(Throwable ex, ServerWebExchange exchange, HttpStatus status) {
        String path = exchange.getRequest().getPath().value();
        String message = determineErrorMessage(ex);

        return String.format(
            "{\"status\": %d, \"error\": \"%s\", \"message\": \"%s\", \"path\": \"%s\"}",
            status.value(),
            status.getReasonPhrase(),
            message,
            path
        );
    }

    private String determineErrorMessage(Throwable ex) {
        return switch (ex) {
            case UserAlreadyExistsException e -> e.getMessage();
            case ConstraintViolationException e -> "Validation failed: " + e.getMessage();
            case IllegalArgumentException e -> "Invalid request data: " + e.getMessage();
            default -> "An unexpected error occurred. Please try again later.";
        };
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, String errorResponse, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] bytes = errorResponse.getBytes();
        return exchange.getResponse().writeWith(
            Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))
        );
    }
}