package co.com.bancolombia.api.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper jsonObjectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Handling exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);

        HttpStatus status = determineHttpStatus(ex);
        ErrorResponse errorResponse = createErrorResponse(ex, exchange, status);

        return writeErrorResponse(exchange, errorResponse, status);
    }

    private HttpStatus determineHttpStatus(Throwable ex) {
        if (isConflictException(ex)) {
            return HttpStatus.CONFLICT;
        }

        return switch (ex) {
            case ConstraintViolationException ignored -> HttpStatus.BAD_REQUEST;
            case IllegalArgumentException ignored -> HttpStatus.BAD_REQUEST;
            case JsonProcessingException ignored -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private boolean isConflictException(Throwable ex) {
        String message = ex.getMessage();
        if (message != null) {
            String lowerMessage = message.toLowerCase();
            return lowerMessage.contains("already exists") ||
                   lowerMessage.contains("ya existe") ||
                   lowerMessage.contains("duplicate") ||
                   lowerMessage.contains("conflict");
        }
        return false;
    }

    private ErrorResponse createErrorResponse(Throwable ex, ServerWebExchange exchange, HttpStatus status) {
        String path = exchange.getRequest().getPath().value();

        return switch (ex) {
            case ConstraintViolationException e -> createValidationErrorResponse(e, path);
            default -> createGenericErrorResponse(ex, path, status);
        };
    }

    private ErrorResponse createValidationErrorResponse(ConstraintViolationException ex, String path) {
        List<FieldError> fieldErrors = ex.getConstraintViolations().stream()
                .map(this::createFieldError)
                .toList();

        return ValidationErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message("Request contains validation errors")
                .path(path)
                .violations(fieldErrors)
                .build();
    }

    private FieldError createFieldError(ConstraintViolation<?> violation) {
        return FieldError.builder()
                .field(violation.getPropertyPath().toString())
                .message(violation.getMessage())
                .build();
    }

    private ErrorResponse createGenericErrorResponse(Throwable ex, String path, HttpStatus status) {
        String message = determineErrorMessage(ex);

        return GenericErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
    }

    private String determineErrorMessage(Throwable ex) {
        if (isConflictException(ex)) {
            return ex.getMessage();
        }

        return switch (ex) {
            case IllegalArgumentException e -> "Invalid request data: " + e.getMessage();
            case JsonProcessingException ignored -> "Invalid JSON format in request";
            default -> "An unexpected error occurred. Please try again later.";
        };
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, ErrorResponse errorResponse, HttpStatus status) {
        try {
            exchange.getResponse().setStatusCode(status);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            String jsonResponse = jsonObjectMapper.writeValueAsString(errorResponse);
            byte[] bytes = jsonResponse.getBytes();

            return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))
            );
        } catch (JsonProcessingException e) {
            log.error("Error serializing error response", e);
            return Mono.empty();
        }
    }

    // Base error response interface
    public interface ErrorResponse {
        int getStatus();
        String getError();
        String getMessage();
        String getPath();
    }

    // Generic error response
    @lombok.Data
    @lombok.Builder
    public static class GenericErrorResponse implements ErrorResponse {
        private int status;
        private String error;
        private String message;
        private String path;
    }

    // Validation error response
    @lombok.Data
    @lombok.Builder
    public static class ValidationErrorResponse implements ErrorResponse {
        private int status;
        private String error;
        private String message;
        private String path;
        private List<FieldError> violations;
    }

    // Field error for validation
    @lombok.Data
    @lombok.Builder
    public static class FieldError {
        private String field;
        private String message;
    }
}