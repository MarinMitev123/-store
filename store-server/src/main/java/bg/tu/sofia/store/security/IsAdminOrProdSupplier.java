package bg.tu.sofia.store.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

// Анотация, която ограничава достъпа до методите само за потребители с роли "ADMIN" или
// "PROD_SUPPLIER"
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or hasRole('PROD_SUPPLIER'))")
public @interface IsAdminOrProdSupplier {}
