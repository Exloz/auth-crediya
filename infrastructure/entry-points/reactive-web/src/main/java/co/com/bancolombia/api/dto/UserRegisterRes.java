package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Successful user registration response")
public record UserRegisterRes(
    @Schema(description = "Unique ID of the registered user", example = "1")
    Long userId,

    @Schema(description = "User's name", example = "Juan")
    String name,

    @Schema(description = "User's last name", example = "Pérez")
    String lastName,

    @Schema(description = "Date of birth", example = "1990-01-15")
    LocalDate birthDate,

    @Schema(description = "Residential address", example = "Calle 123 #45-67")
    String address,

    @Schema(description = "Email address", example = "juan.perez@email.com")
    String email,

    @Schema(description = "Monthly base salary", example = "2500000.00")
    BigDecimal baseSalary,

    @Schema(description = "Phone number", example = "+57 300 123 4567")
    String phoneNumber
) {
}
