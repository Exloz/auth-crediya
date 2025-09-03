package co.com.bancolombia.api.dto.register;

import co.com.bancolombia.api.dto.utils.ValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Data to register a new user")
public record UserRegisterReq(
        @Schema(description = "User's name", example = "Juan")
        @NotBlank(message = ValidationMessages.NAME_REQUIRED)
        String name,

        @Schema(description = "User's last name", example = "Pérez")
        @NotBlank(message = ValidationMessages.LASTNAME_REQUIRED)
        String lastName,

        @Schema(description = "Date of birth", example = "1990-01-15")
        @NotNull(message = ValidationMessages.BIRTH_DATE_REQUIRED)
        @Past
        LocalDate birthDate,

        @Schema(description = "Residential address", example = "Calle 123 #45-67")
        String address,

        @Schema(description = "Identity document number", example = "12345678")
        @NotBlank(message = ValidationMessages.ID_DOCUMENT_REQUIRED)
        String idDocument,

        @Schema(description = "Email address", example = "juan.perez@email.com")
        @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
        @Email(message = ValidationMessages.EMAIL_INVALID_FORMAT)
        String email,

        @Schema(description = "Monthly base salary", example = "2500000.00")
        @DecimalMin(value = "0.0", inclusive = false, message = ValidationMessages.BASE_SALARY_MIN_VALUE)
        @DecimalMax(value = "15000000.0", message = ValidationMessages.BASE_SALARY_MAX_VALUE)
        @NotNull(message = ValidationMessages.BASE_SALARY_REQUIRED)
        BigDecimal baseSalary,

        @Schema(description = "Phone number", example = "+57 300 123 4567")
        @NotBlank(message = ValidationMessages.PHONE_REQUIRED)
        String phoneNumber,

        @Schema(description = "User password", example = "securePassword123")
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,

        @Schema(description = "Role to assign (optional)", example = "USER", allowableValues = {"USER", "ADMIN", "ASESOR"})
        String role
) {
}
