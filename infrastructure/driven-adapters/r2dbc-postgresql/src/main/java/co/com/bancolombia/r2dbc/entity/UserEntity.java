package co.com.bancolombia.r2dbc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
