package co.com.bancolombia.r2dbc.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

import java.time.Duration;

@Configuration
@EnableR2dbcRepositories(basePackages = "co.com.bancolombia.r2dbc")
public class PostgreSQLConnectionPool {
    /* Change these values for your project */
    public static final int INITIAL_SIZE = 12;
    public static final int MAX_SIZE = 15;
    public static final int MAX_IDLE_TIME = 30;
    public static final int DEFAULT_PORT = 5432;

 	@Bean
 	@Primary
 	public io.r2dbc.spi.ConnectionFactory connectionFactory(PostgresqlConnectionProperties properties) {
 		return ConnectionFactoryBuilder.withUrl("r2dbc:postgresql://" + properties.host() + ":" + properties.port() + "/" + properties.database())
                 .username(properties.username())
                 .password(properties.password())
                 .build();
 	}

 	@Bean
 	public R2dbcEntityTemplate r2dbcEntityTemplate(io.r2dbc.spi.ConnectionFactory connectionFactory) {
 		return new R2dbcEntityTemplate(connectionFactory);
 	}

 	@Bean
 	public ConnectionPool getConnectionConfig(io.r2dbc.spi.ConnectionFactory connectionFactory) {
         ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder()
                 .connectionFactory(connectionFactory)
                 .name("api-postgres-connection-pool")
                 .initialSize(INITIAL_SIZE)
                 .maxSize(MAX_SIZE)
                 .maxIdleTime(Duration.ofMinutes(MAX_IDLE_TIME))
                 .validationQuery("SELECT 1")
                 .build();

 		return new ConnectionPool(poolConfiguration);
 	}
}