package co.com.bancolombia.r2dbc.entity;

import co.com.bancolombia.model.user.RoleId;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    @Column("user_id")
    private Long userId;

    @Column("name")
    private String name;

    @Column("lastName")
    private String lastName;

    @Column("email")
    private String email;

    @Column("id_document")
    private String idDocument;

    @Column("phone_number")
    private String phoneNumber;

    @Column("address")
    private String address;

    @Column("birth_date")
    private LocalDate birthDate;

    @Column("role_id")
    private RoleId roleId;

    @Column("base_salary")
    private BigDecimal baseSalary;
}
