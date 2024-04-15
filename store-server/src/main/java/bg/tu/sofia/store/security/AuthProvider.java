package bg.tu.sofia.store.security;

import bg.tu.sofia.store.model.User;
import bg.tu.sofia.store.service.UserService;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthProvider implements AuthenticationProvider {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public AuthProvider(UserService userService) {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.userService = userService;
    }

    // Метод за аутентикация
    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String username = authentication.getName();
        User account = userService.getUserByUsername(username);

        // Проверка дали потребителят съществува в системата
        if (account == null) {
            return null;
        }

        String credentials = String.valueOf(authentication.getCredentials());

        // Проверка за съвпадение на паролите
        if (!passwordEncoder.matches(credentials, account.getPassword())) {
            return null;
        }

        // Създаване на списък с ролите на потребителя
        List<GrantedAuthority> roles =
                Collections.singletonList(() -> account.getRole().toString().toUpperCase());

        // Създаване на успешна аутентикация
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        account, authentication.getCredentials(), roles);
        log.info("Authentication successful");
        return auth;
    }

    // Поддържане на аутентикационния обект за потребителско име и парола
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
