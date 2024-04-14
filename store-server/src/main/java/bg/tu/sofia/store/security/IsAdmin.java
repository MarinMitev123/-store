package bg.tu.sofia.store.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

// Анотация, която ограничава достъпа до методите само за потребители с роля "ADMIN"
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
public @interface IsAdmin {}
