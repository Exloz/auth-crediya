package co.com.bancolombia.usecase.user;

public interface PasswordEncoderPort {

    String encodePassword(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}

