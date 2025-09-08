package co.com.bancolombia.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("credentials")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CredentialsEntity {
    @Id
    @Column("user_id")
    private Long userId;

    @Column("password")
    private String password;
}

