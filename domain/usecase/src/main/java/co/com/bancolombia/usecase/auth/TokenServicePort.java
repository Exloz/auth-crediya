package co.com.bancolombia.usecase.auth;

import java.util.Map;

public interface TokenServicePort {

    String generateToken(String email, Long userId, String role);

    Map<String, Object> validateToken(String token);
}