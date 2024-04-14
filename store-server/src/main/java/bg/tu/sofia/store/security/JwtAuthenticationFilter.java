package bg.tu.sofia.store.security;

import bg.tu.sofia.store.utils.JsonUtil;
import com.google.gson.Gson;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Филтър за удостоверяване с JWT токен
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    // Gson инстанция за сериализация и десериализация на JSON данни
    private static final Gson gson = new Gson();
    // Време на валидност на JWT токена
    private static final Duration TOKEN_TTL = Duration.of(3, ChronoUnit.HOURS);
    // Мениджър за удостоверяване на потребители
    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        // Задаване на URL адреса за удостоверяване
        setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL);
        // Задаване на обработчик за неуспешно удостоверяване
        setAuthenticationFailureHandler(new AuthenticationErrorHandler());
    }

    // Опит за удостоверяване на потребител
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response) {
        log.info("Attempting auth {}", request.getRequestURI());
        // Четене на потребителските данни от заявката и създаване на обект Credentials
        Credentials credentials = gson.fromJson(request.getReader(), Credentials.class);
        // Създаване на токен за удостоверяване
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        credentials.getUsername(), credentials.getPassword(), new ArrayList<>());
        // Удостоверяване на потребителя
        return authenticationManager.authenticate(authenticationToken);
    }

    // Обработка на успешното удостоверяване
    @Override
    @SneakyThrows
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain,
            Authentication authentication) {
        UsernamePasswordAuthenticationToken user =
                ((UsernamePasswordAuthenticationToken) authentication);

        // Извличане на ролите на потребителя
        List<String> roles =
                user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());
        byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();

        // Генериране на JWT токен
        String token =
                Jwts.builder()
                        .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
                        .setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)
                        .setIssuer(SecurityConstants.TOKEN_ISSUER)
                        .setAudience(SecurityConstants.TOKEN_AUDIENCE)
                        .setSubject(JsonUtil.toStringObject(user.getPrincipal()))
                        .setExpiration(new Date(System.currentTimeMillis() + TOKEN_TTL.toMillis()))
                        .claim("role", roles)
                        .compact();

        // Добавяне на токена в отговора
        response.addHeader(SecurityConstants.TOKEN_HEADER, SecurityConstants.BEARER_PREFIX + token);
        // Създаване на бисквитка с токена
        Cookie cookie = new Cookie(SecurityConstants.ACCESS_TOKEN, token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(((int) TOKEN_TTL.getSeconds()));
        // Добавяне на бисквитката към отговора
        response.addCookie(cookie);

        // Изпращане на потребителските данни като JSON обект
        response.getOutputStream().print(JsonUtil.toStringObject(user.getPrincipal()));
    }
}
