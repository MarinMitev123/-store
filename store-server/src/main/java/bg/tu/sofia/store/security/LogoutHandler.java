package bg.tu.sofia.store.security;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

// Компонент, отговарящ за обработка на успешно излизане от системата
@Component
public class LogoutHandler implements LogoutSuccessHandler {

    // Метод, извикван при успешно излизане от системата
    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {
        // Премахване на токена от бисквитката и връщане на успешен HTTP статус
        response.addCookie(new Cookie(SecurityConstants.ACCESS_TOKEN, null));
        response.setStatus(200);
    }
}
