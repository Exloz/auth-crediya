package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Respuesta del registro de usuario exitoso")
public record UserRegisterRes(
    @Schema(description = "ID único del usuario registrado", example = "1")
    Long userId,

    @Schema(description = "Nombre del usuario", example = "Juan")
    String name,

    @Schema(description = "Apellido del usuario", example = "Pérez")
    String lastName,

    @Schema(description = "Fecha de nacimiento", example = "1990-01-15")
    LocalDate birthDate,

    @Schema(description = "Dirección de residencia", example = "Calle 123 #45-67")
    String address,

    @Schema(description = "Correo electrónico", example = "juan.perez@email.com")
    String email,

    @Schema(description = "Salario base mensual", example = "2500000.00")
    BigDecimal baseSalary,

    @Schema(description = "Número de teléfono", example = "+57 300 123 4567")
    String phoneNumber
) {
}
