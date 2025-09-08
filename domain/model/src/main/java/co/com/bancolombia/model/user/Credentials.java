package co.com.bancolombia.model.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Credentials {
    private Long userId;
    private String password;
}

