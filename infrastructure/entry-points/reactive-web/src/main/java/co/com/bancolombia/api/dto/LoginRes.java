package co.com.bancolombia.api.dto;

import co.com.bancolombia.model.user.RoleId;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login success response")
public record LoginRes(
        @Schema(description = "User id", example = "1")
        Long userId,
        @Schema(description = "User email", example = "user@example.com")
        String email,
        @Schema(description = "User role", example = "USER")
        RoleId roleId,
        @Schema(description = "JWT token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {}

