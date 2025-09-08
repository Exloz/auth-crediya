package co.com.bancolombia.bootstrap;

import co.com.bancolombia.model.user.Credentials;
import co.com.bancolombia.model.user.RoleId;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.CredentialsRepository;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.usecase.portUtils.PasswordEncoderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootstrapAdminInitializer implements ApplicationRunner {

    private static final String BOOTSTRAP_DISABLED = "Bootstrap admin seeding is disabled";
    private static final String BOOTSTRAP_START = "Starting bootstrap admin seeding";
    private static final String BOOTSTRAP_SKIP_MISSING = "Bootstrap admin email or password not configured; skipping";
    private static final String BOOTSTRAP_USER_EXISTS = "Bootstrap admin user already exists: {}";
    private static final String BOOTSTRAP_USER_CREATED = "Bootstrap admin user created: {}";
    private static final String BOOTSTRAP_CREDS_EXIST = "Credentials already exist for userId={}; skipping";
    private static final String BOOTSTRAP_CREDS_CREATED = "Credentials created for userId={}";

    private final BootstrapProperties properties;
    private final UserRepository userRepository;
    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoderPort passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (!properties.isEnabled()) {
            log.info(BOOTSTRAP_DISABLED);
            return;
        }

        final var admin = properties.getAdmin();
        if (admin.getEmail() == null || admin.getEmail().isBlank() ||
                admin.getPassword() == null || admin.getPassword().isBlank()) {
            log.warn(BOOTSTRAP_SKIP_MISSING);
            return;
        }

        log.info(BOOTSTRAP_START);

        seedAdmin(admin.getEmail().trim(), admin.getPassword(), admin.getName(), admin.getLastName(), admin.getIdDocument(), admin.getPhoneNumber())
                .doOnError(err -> log.warn("Bootstrap admin seeding completed with warnings: {}", err.getMessage()))
                .doOnSuccess(result -> log.info("Bootstrap admin seeding completed successfully"))
                .onErrorComplete() // Continue even if there's an error
                .subscribe(); // Non-blocking per project guidelines
    }

    private Mono<Void> seedAdmin(String email, String password, String name, String lastName, String idDocument, String phoneNumber) {
        return userRepository.findByEmail(email)
                .doOnNext(u -> log.info(BOOTSTRAP_USER_EXISTS, email))
                .switchIfEmpty(createAdminUser(email, name, lastName, idDocument, phoneNumber)
                        .doOnNext(u -> log.info(BOOTSTRAP_USER_CREATED, email)))
                .flatMap(user -> ensureCredentials(user, password))
                .then();
    }

    private Mono<User> createAdminUser(String email, String name, String lastName, String idDocument, String phoneNumber) {
        var user = User.builder()
                .name(name)
                .lastName(lastName)
                .email(email)
                .idDocument(idDocument)
                .phoneNumber(phoneNumber)
                .roleId(RoleId.ADMIN)
                .build();
        return userRepository.saveUser(user);
    }

    private Mono<Credentials> ensureCredentials(User user, String password) {
        // First check if credentials already exist
        return credentialsRepository.findByUserId(user.getUserId())
                .doOnNext(c -> log.info(BOOTSTRAP_CREDS_EXIST, user.getUserId()))
                .switchIfEmpty(Mono.defer(() -> {
                    // If not found, create new credentials
                    String hashedPassword = passwordEncoder.encodePassword(password);
                    var creds = Credentials.builder()
                            .userId(user.getUserId())
                            .password(hashedPassword)
                            .build();
                    return credentialsRepository.save(creds)
                            .doOnNext(c -> log.info(BOOTSTRAP_CREDS_CREATED, user.getUserId()));
                }));
    }
}

