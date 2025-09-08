package co.com.bancolombia.model.user;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUserWithBuilder() {
        // Given
        LocalDate birthDate = LocalDate.of(1990, 1, 15);
        BigDecimal baseSalary = new BigDecimal("2500000.00");

        // When
        User user = User.builder()
            .userId(1L)
            .name("Juan")
            .lastName("Pérez")
            .email("juan.perez@email.com")
            .idDocument("12345678")
            .phoneNumber("+57 300 123 4567")
            .address("Calle 123 #45-67")
            .birthDate(birthDate)
            .roleId(RoleId.USER)
            .baseSalary(baseSalary)
            .build();

        // Then
        assertEquals(1L, user.getUserId());
        assertEquals("Juan", user.getName());
        assertEquals("Pérez", user.getLastName());
        assertEquals("juan.perez@email.com", user.getEmail());
        assertEquals("12345678", user.getIdDocument());
        assertEquals("+57 300 123 4567", user.getPhoneNumber());
        assertEquals("Calle 123 #45-67", user.getAddress());
        assertEquals(birthDate, user.getBirthDate());
        assertEquals(RoleId.USER, user.getRoleId());
        assertEquals(baseSalary, user.getBaseSalary());
    }

    @Test
    void shouldCreateUserWithNoArgsConstructor() {
        // When
        User user = new User();

        // Then
        assertNull(user.getUserId());
        assertNull(user.getName());
        assertNull(user.getLastName());
        assertNull(user.getEmail());
        assertNull(user.getIdDocument());
        assertNull(user.getPhoneNumber());
        assertNull(user.getAddress());
        assertNull(user.getBirthDate());
        assertNull(user.getRoleId());
        assertNull(user.getBaseSalary());
    }

    @Test
    void shouldCreateUserWithAllArgsConstructor() {
        // Given
        LocalDate birthDate = LocalDate.of(1990, 1, 15);
        BigDecimal baseSalary = new BigDecimal("2500000.00");

        // When
        User user = new User(
            1L,
            "Juan",
            "Pérez",
            "juan.perez@email.com",
            "12345678",
            "+57 300 123 4567",
            "Calle 123 #45-67",
            birthDate,
            RoleId.USER,
            baseSalary
        );

        // Then
        assertEquals(1L, user.getUserId());
        assertEquals("Juan", user.getName());
        assertEquals("Pérez", user.getLastName());
        assertEquals("juan.perez@email.com", user.getEmail());
        assertEquals("12345678", user.getIdDocument());
        assertEquals("+57 300 123 4567", user.getPhoneNumber());
        assertEquals("Calle 123 #45-67", user.getAddress());
        assertEquals(birthDate, user.getBirthDate());
        assertEquals(RoleId.USER, user.getRoleId());
        assertEquals(baseSalary, user.getBaseSalary());
    }

    @Test
    void shouldUseToBuilder() {
        // Given
        User originalUser = User.builder()
            .userId(1L)
            .name("Juan")
            .email("juan.perez@email.com")
            .build();

        // When
        User updatedUser = originalUser.toBuilder()
            .lastName("Pérez")
            .phoneNumber("+57 300 123 4567")
            .build();

        // Then
        assertEquals(1L, updatedUser.getUserId());
        assertEquals("Juan", updatedUser.getName());
        assertEquals("Pérez", updatedUser.getLastName());
        assertEquals("juan.perez@email.com", updatedUser.getEmail());
        assertEquals("+57 300 123 4567", updatedUser.getPhoneNumber());
    }

    @Test
    void shouldHandleNullValues() {
        // When
        User user = User.builder().build();

        // Then
        assertNull(user.getUserId());
        assertNull(user.getName());
        assertNull(user.getLastName());
        assertNull(user.getEmail());
        assertNull(user.getIdDocument());
        assertNull(user.getPhoneNumber());
        assertNull(user.getAddress());
        assertNull(user.getBirthDate());
        assertNull(user.getRoleId());
        assertNull(user.getBaseSalary());
    }
}