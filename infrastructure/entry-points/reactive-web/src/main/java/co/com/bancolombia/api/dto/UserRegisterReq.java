package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Datos para registrar un nuevo usuario")
public record UserRegisterReq(
        @Schema(description = "Nombre del usuario", example = "Juan")
        @NotBlank(message = ValidationMessages.NAME_REQUIRED)
        String name,

        @Schema(description = "Apellido del usuario", example = "Pérez")
        @NotBlank(message = ValidationMessages.LASTNAME_REQUIRED)
        String lastName,

        @Schema(description = "Fecha de nacimiento", example = "1990-01-15")
        @NotNull(message = ValidationMessages.BIRTH_DATE_REQUIRED)
        LocalDate birthDate,

        @Schema(description = "Dirección de residencia", example = "Calle 123 #45-67")
        String address,

        @Schema(description = "Número de documento de identidad", example = "12345678")
        String idDocument,

        @Schema(description = "Correo electrónico", example = "juan.perez@email.com")
        @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
        @Email(message = ValidationMessages.EMAIL_INVALID_FORMAT)
        String email,

        @Schema(description = "Salario base mensual", example = "2500000.00")
        @NotNull(message = ValidationMessages.BASE_SALARY_REQUIRED)
        @DecimalMin(value = "0.0", inclusive = false, message = ValidationMessages.BASE_SALARY_MIN_VALUE)
        @DecimalMax(value = "15000000.0", message = ValidationMessages.BASE_SALARY_MAX_VALUE)
        BigDecimal baseSalary,

        @Schema(description = "Número de teléfono", example = "+57 300 123 4567")
        String phoneNumber
) {
}
