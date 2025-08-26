package co.com.bancolombia.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.Date;

@Table("users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    @Column("user_id")
    private String userId;

    @Column("name")
    private String name;

    @Column("lastname")
    private String lastname;

    @Column("email")
    private String email;

    @Column("id_document")
    private String idDocument;

    @Column("phone_number")
    private String phoneNumber;

    @Column("address")
    private String address;

    @Column("birth_date")
    private Date birthDate;

    @Column("role_id")
    private String roleId;

    @Column("base_salary")
    private BigDecimal baseSalary;
}
