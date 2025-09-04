package co.com.bancolombia.model.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleIdTest {

    @Test
    void shouldHaveAllExpectedValues() {
        // When & Then
        assertEquals(3, RoleId.values().length);
        assertTrue(RoleId.valueOf("USER") instanceof RoleId);
        assertTrue(RoleId.valueOf("ADMIN") instanceof RoleId);
        assertTrue(RoleId.valueOf("ADVISOR") instanceof RoleId);
    }

    @Test
    void shouldReturnCorrectValues() {
        // When & Then
        assertEquals(RoleId.USER, RoleId.valueOf("USER"));
        assertEquals(RoleId.ADMIN, RoleId.valueOf("ADMIN"));
        assertEquals(RoleId.ADVISOR, RoleId.valueOf("ADVISOR"));
    }

    @Test
    void shouldThrowExceptionForInvalidValue() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> RoleId.valueOf("INVALID"));
    }

    @Test
    void shouldHaveCorrectNames() {
        // When & Then
        assertEquals("USER", RoleId.USER.name());
        assertEquals("ADMIN", RoleId.ADMIN.name());
        assertEquals("ADVISOR", RoleId.ADVISOR.name());
    }

    @Test
    void shouldHaveCorrectOrdinals() {
        // When & Then
        assertEquals(0, RoleId.USER.ordinal());
        assertEquals(1, RoleId.ADMIN.ordinal());
        assertEquals(2, RoleId.ADVISOR.ordinal());
    }

    @Test
    void shouldBeEqualToItself() {
        // When & Then
        assertEquals(RoleId.USER, RoleId.USER);
        assertEquals(RoleId.ADMIN, RoleId.ADMIN);
        assertEquals(RoleId.ADVISOR, RoleId.ADVISOR);
    }

    @Test
    void shouldNotBeEqualToDifferentValues() {
        // When & Then
        assertNotEquals(RoleId.USER, RoleId.ADMIN);
        assertNotEquals(RoleId.USER, RoleId.ADVISOR);
        assertNotEquals(RoleId.ADMIN, RoleId.ADVISOR);
    }
}