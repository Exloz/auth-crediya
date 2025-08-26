package co.com.bancolombia.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Date;

public record UserRegisterReq(
        @NotBlank(message = ValidationMessages.NAME_REQUIRED)
        String name,

        @NotBlank(message = ValidationMessages.LASTNAME_REQUIRED)
        String lastName,

        @NotNull(message = ValidationMessages.BIRTH_DATE_REQUIRED)
        Date birthDate,

        String address,

        @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
        @Email(message = ValidationMessages.EMAIL_INVALID_FORMAT)
        String email,

        @NotNull(message = ValidationMessages.BASE_SALARY_REQUIRED)
        @DecimalMin(value = "0.0", inclusive = false, message = ValidationMessages.BASE_SALARY_MIN_VALUE)
        @DecimalMax(value = "15000000.0", inclusive = true, message = ValidationMessages.BASE_SALARY_MAX_VALUE)
        BigDecimal baseSalary,

        String phoneNumber
) {
}
