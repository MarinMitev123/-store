package bg.tu.sofia.store.security;

import bg.tu.sofia.store.exception.ErrorCode;
import bg.tu.sofia.store.utils.JsonUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class AuthenticationErrorHandler
        implements AuthenticationFailureHandler, AuthenticationEntryPoint {

    // Обработва неуспешната аутентикация и връща подходящо съобщение за грешка на клиента
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException, ServletException {
        handleUnauthorized(response, "Authentication unsuccessful! Wrong or empty credentials!");
    }

    // Обработва липсата на авторизация и връща подходящо съобщение за грешка на клиента
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {
        handleUnauthorized(response, "You are not authorized to execute this operation!");
    }

    // Помощен метод за обработка на грешката и връщане на подходящ HTTP отговор на клиента
    private void handleUnauthorized(HttpServletResponse response, String message)
            throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.builder().errorMessage(message).build();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getOutputStream().println(JsonUtil.toStringObject(errorCode));
        response.getOutputStream().flush();
    }
}
