package co.com.bancolombia.api.dto.user;

import co.com.bancolombia.model.user.RoleId;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "User information response")
public record UserInfoRes(
    @Schema(description = "Unique ID of the user", example = "1")
    Long userId,

    @Schema(description = "User's name", example = "Juan")
    String name,

    @Schema(description = "User's last name", example = "Pérez")
    String lastName,

    @Schema(description = "Email address", example = "juan.perez@email.com")
    String email,

    @Schema(description = "Document ID number", example = "1234567890")
    String idDocument,

    @Schema(description = "Phone number", example = "+57 300 123 4567")
    String phoneNumber,

    @Schema(description = "Residential address", example = "Calle 123 #45-67")
    String address,

    @Schema(description = "Date of birth", example = "1990-01-15")
    LocalDate birthDate,

    @Schema(description = "User role", example = "USER")
    RoleId roleId,

    @Schema(description = "Monthly base salary", example = "2500000.00")
    BigDecimal baseSalary
) {
}