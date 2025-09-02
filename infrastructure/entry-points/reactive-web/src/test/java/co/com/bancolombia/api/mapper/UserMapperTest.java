package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.UserRegisterReq;
import co.com.bancolombia.api.dto.UserRegisterRes;
import co.com.bancolombia.model.user.RoleId;
import co.com.bancolombia.model.user.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void shouldMapUserRegisterReqToUserWithDefaultRole() {
        // Given
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        UserRegisterReq request = new UserRegisterReq(
                "John",
                "Doe",
                birthDate,
                "Calle 123",
                "12345678",
                "john.doe@example.com",
                BigDecimal.valueOf(1000000),
                "3001234567",
                "USER"
        );

        // When
        User user = userMapper.toModel(request);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(user.getIdDocument()).isEqualTo("12345678");
        assertThat(user.getPhoneNumber()).isEqualTo("3001234567");
        assertThat(user.getAddress()).isEqualTo("Calle 123");
        assertThat(user.getBirthDate()).isEqualTo(request.birthDate());
        assertThat(user.getBaseSalary()).isEqualTo(BigDecimal.valueOf(1000000));
        assertThat(user.getRoleId()).isEqualTo(RoleId.USER);
        assertThat(user.getUserId()).isNull(); // Should be ignored
    }

    @Test
    void shouldMapUserToUserRegisterRes() {
        // Given
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        User user = User.builder()
                .userId(1L)
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .idDocument("12345678")
                .phoneNumber("3001234567")
                .address("Calle 123")
                .birthDate(birthDate)
                .roleId(RoleId.USER)
                .baseSalary(BigDecimal.valueOf(1000000))
                .build();

        // When
        UserRegisterRes response = userMapper.toResponse(user);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("John");
        assertThat(response.lastName()).isEqualTo("Doe");
        assertThat(response.email()).isEqualTo("john.doe@example.com");
        assertThat(response.phoneNumber()).isEqualTo("3001234567");
        assertThat(response.address()).isEqualTo("Calle 123");
        assertThat(response.birthDate()).isEqualTo(user.getBirthDate());
        assertThat(response.baseSalary()).isEqualTo(BigDecimal.valueOf(1000000));
    }
}
