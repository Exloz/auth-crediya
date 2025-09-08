package co.com.bancolombia.bootstrap;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "bootstrap")
public class BootstrapProperties {
    private boolean enabled = true;
    private Admin admin = new Admin();

    @Getter
    @Setter
    public static class Admin {
        private String email;
        private String password;
        private String name = "Bootstrap";
        private String lastName = "Admin";
        private String idDocument = "";
        private String phoneNumber = "";
    }
}

