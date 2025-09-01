package co.com.bancolombia;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
public class MainApplication {

    private static final String APPLICATION_STARTED_SUCCESSFULLY = "Auth Crediya Application started successfully";

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
        log.info(APPLICATION_STARTED_SUCCESSFULLY);
    }
}
