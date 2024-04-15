package bg.tu.sofia.store.security;

import bg.tu.sofia.store.model.User;
import bg.tu.sofia.store.service.UserService;
import bg.tu.sofia.store.utils.JsonUtil;
import bg.tu.sofia.store.utils.SecurityUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

// Филтър за удостоверяване с JWT токен при извършване на операции
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserService userService;

    public JwtAuthorizationFilter(
            AuthenticationManager authenticationManager, UserService userService) {
        super(authenticationManager);
        this.userService = userService;
    }

    // Извиква се при извършване на операции
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        // Проверка дали токенът е наличен
        if (!extractToken(request).isPresent()) {
            filterChain.doFilter(request, response);
            return;
        }
        // Удостоверяване на потребителя
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

        // Задаване на удостоверението в контекста на сигурността
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    // Извличане на потребителското удостоверение от JWT токена
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        Optional<String> token = extractToken(request);
        if (token.isPresent()) {
            try {
                // Декодиране на JWT токена
                Jws<Claims> parsedToken = SecurityUtil.decryptJwsToken(token.get());

                // Извличане на потребителските данни от токена
                String accountRaw = parsedToken.getBody().getSubject();

                // Извличане на ролите на потребителя от токена
                List<SimpleGrantedAuthority> authorities =
                        ((List<?>) parsedToken.getBody().get(SecurityConstants.ROLE_KEY))
                                .stream()
                                        .map(
                                                authority ->
                                                        new SimpleGrantedAuthority(
                                                                "ROLE_" + authority))
                                        .collect(Collectors.toList());

                // Проверка дали съществуват потребителски данни
                if (!StringUtils.isEmpty(accountRaw)) {
                    User account = JsonUtil.mapObject(accountRaw, User.class);
                    log.info("Account in token: {}", account);
                    User userDb = userService.getUserById(account.getId());
                    log.info("Account in db: {}", account);
                    // Проверка на потребителските данни от токена със записите в базата данни
                    if (account.getId() != null
                            && userDb.getId().equals(account.getId())
                            && userDb.getUsername().equals(account.getUsername())
                            && userDb.getPassword().equals(account.getPassword())) {
                        return new UsernamePasswordAuthenticationToken(userDb, null, authorities);
                    } else {
                        log.warn("Token provided, but wrong credentials!");
                    }
                }
            } catch (ExpiredJwtException exception) {
                log.warn(
                        "Request to parse expired JWT : {} failed : {}",
                        token,
                        exception.getMessage());
            } catch (UnsupportedJwtException exception) {
                log.warn(
                        "Request to parse unsupported JWT : {} failed : {}",
                        token,
                        exception.getMessage());
            } catch (MalformedJwtException exception) {
                log.warn(
                        "Request to parse invalid JWT : {} failed : {}",
                        token,
                        exception.getMessage());
            } catch (SignatureException exception) {
                log.warn(
                        "Request to parse JWT with invalid signature : {} failed : {}",
                        token,
                        exception.getMessage());
            } catch (IllegalArgumentException exception) {
                log.warn(
                        "Request to parse empty or null JWT : {} failed : {}",
                        token,
                        exception.getMessage());
            }
        }

        return null;
    }

    // Извличане на JWT токена от заявката
    private static Optional<String> extractToken(HttpServletRequest request) {
        return Optional.ofNullable(
                        extractCookieVal(request, SecurityConstants.ACCESS_TOKEN)
                                .orElse(request.getHeader(SecurityConstants.TOKEN_HEADER)))
                .map(header -> header.replace(SecurityConstants.BEARER_PREFIX, ""));
    }

    // Извличане на стойността на бисквитка от заявката
    private static Optional<String> extractCookieVal(
            HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findFirst()
                .map(Cookie::getValue);
    }
}
