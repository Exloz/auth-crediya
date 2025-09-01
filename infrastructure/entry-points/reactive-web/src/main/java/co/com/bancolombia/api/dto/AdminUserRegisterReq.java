package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data to register a new privileged user (ADMIN/ASESOR)")
public record AdminUserRegisterReq(
        @Schema(description = "User's name", example = "Ana")
        @NotBlank(message = ValidationMessages.NAME_REQUIRED)
        String name,

        @Schema(description = "User's last name", example = "García")
        @NotBlank(message = ValidationMessages.LASTNAME_REQUIRED)
        String lastName,

        @Schema(description = "Email address", example = "ana.garcia@acme.com")
        @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
        @Email(message = ValidationMessages.EMAIL_INVALID_FORMAT)
        String email,

        @Schema(description = "Identity document number", example = "10203040")
        @NotBlank(message = ValidationMessages.ID_DOCUMENT_REQUIRED)
        String idDocument,

        @Schema(description = "Phone number", example = "+57 300 000 0000")
        @NotBlank(message = ValidationMessages.PHONE_REQUIRED)
        String phoneNumber,

        @Schema(description = "Role to assign", example = "ADMIN", allowableValues = {"ADMIN","ASESOR"})
        @NotBlank(message = ValidationMessages.ROLE_REQUIRED)
        String role
) {
}

