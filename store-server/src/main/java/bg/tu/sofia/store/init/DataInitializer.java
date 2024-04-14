package bg.tu.sofia.store.init; // Промяна на пакета

import bg.tu.sofia.store.model.User;
import bg.tu.sofia.store.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Starting data initialization  ...");

        User adminAccount =
                User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .firstName("ADMIN")
                        .lastName("ADMIN")
                        .role(bg.tu.sofia.store.model.enums.Role.ADMIN)
                        .build();
        userService.createUser(adminAccount, true);
    }
}

