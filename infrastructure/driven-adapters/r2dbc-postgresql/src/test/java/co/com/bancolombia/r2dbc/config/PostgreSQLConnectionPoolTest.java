package co.com.bancolombia.r2dbc.config;

import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PostgreSQLConnectionPoolTest {

    private final PostgreSQLConnectionPool connectionPool = new PostgreSQLConnectionPool();

    @Test
    void getConnectionConfigSuccess() {
        // Create a real PostgresqlConnectionFactory for testing
        PostgresqlConnectionConfiguration config = PostgresqlConnectionConfiguration.builder()
            .host("localhost")
            .port(PostgreSQLConnectionPool.DEFAULT_PORT)
            .database("test")
            .username("test")
            .password("test")
            .build();

        PostgresqlConnectionFactory connectionFactory = new PostgresqlConnectionFactory(config);

        // Test that the method doesn't throw an exception and returns a ConnectionPool
        assertNotNull(connectionPool.getConnectionConfig(connectionFactory));
    }

    @Test
    void testConnectionPoolConstants() {
        // Test that the constants are properly defined
        assertEquals(12, PostgreSQLConnectionPool.INITIAL_SIZE);
        assertEquals(15, PostgreSQLConnectionPool.MAX_SIZE);
        assertEquals(30, PostgreSQLConnectionPool.MAX_IDLE_TIME);
        assertEquals(5432, PostgreSQLConnectionPool.DEFAULT_PORT);
    }
}
