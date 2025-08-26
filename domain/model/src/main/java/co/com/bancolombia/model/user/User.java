package co.com.bancolombia.model.user;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Long userId;
    private String name;
    private String lastName;
    private String email;
    private String idDocument;
    private String phoneNumber;
    private String address;
    private LocalDate birthDate;
    private RoleId roleId;
    private BigDecimal baseSalary;
}
