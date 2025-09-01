package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login request payload")
public record LoginReq(
        @Schema(description = "User email", example = "user@example.com")
        @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
        @Email(message = ValidationMessages.EMAIL_INVALID_FORMAT)
        String email,

        @Schema(description = "User password", example = "secret")
        @NotBlank(message = "Password is required and cannot be blank")
        String password
) {}

