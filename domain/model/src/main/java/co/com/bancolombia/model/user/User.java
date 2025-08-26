package co.com.bancolombia.model.user;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private String userId;
    private String name;
    private String lastname;
    private String email;
    private String idDocument;
    private String phoneNumber;
    private String address;
    private Date birthDate;
    private String roleId;
    private BigDecimal baseSalary;
}
