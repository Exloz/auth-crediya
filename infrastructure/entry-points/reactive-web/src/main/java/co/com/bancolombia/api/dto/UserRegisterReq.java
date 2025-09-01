package co.com.bancolombia.api.dto;

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
        LocalDate birthDate,

        @Schema(description = "Residential address", example = "Calle 123 #45-67")
        String address,

        @Schema(description = "Identity document number", example = "12345678")
        String idDocument,

        @Schema(description = "Email address", example = "juan.perez@email.com")
        @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
        @Email(message = ValidationMessages.EMAIL_INVALID_FORMAT)
        String email,

        @Schema(description = "Monthly base salary", example = "2500000.00")
        @NotNull(message = ValidationMessages.BASE_SALARY_REQUIRED)
        @DecimalMin(value = "0.0", inclusive = false, message = ValidationMessages.BASE_SALARY_MIN_VALUE)
        @DecimalMax(value = "15000000.0", message = ValidationMessages.BASE_SALARY_MAX_VALUE)
        BigDecimal baseSalary,

        @Schema(description = "Phone number", example = "+57 300 123 4567")
        String phoneNumber
) {
}
