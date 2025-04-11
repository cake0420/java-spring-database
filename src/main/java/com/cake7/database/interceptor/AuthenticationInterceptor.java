package com.cake7.database.interceptor;

import com.cake7.database.repository.UserSessionRepository;
import com.cake7.database.util.Convert;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

public class AuthenticationInterceptor implements HandlerInterceptor {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);
    private final UserSessionRepository userSessionRepository;
    private final Convert convert;

    public AuthenticationInterceptor(UserSessionRepository userSessionRepository, Convert convert) {
        this.userSessionRepository = userSessionRepository;
        this.convert = convert;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 쿠키에서 SESSION_ID를 찾는다
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("SESSION_ID".equals(cookie.getName())) {
                    String sessionId = cookie.getValue();
                    UUID uuid = UUID.fromString(sessionId);
                    // 2. DB에서 유효한 세션인지 확인한다
                    boolean exists = userSessionRepository.existsById(convert.uuidToBytes(uuid));
                    if (exists) {
                        // 3. 세션에 다시 넣어주면 JSP에서도 sessionScope로 접근 가능
                        request.getSession(true).setAttribute("SESSION_ID", sessionId);
                        logger.debug("Authenticated via cookie. Path = {}", request.getRequestURI());
                        return true;
                    }
                }
            }
        }

        // 인증 실패
        logger.debug("Unauthorized access to {}", request.getRequestURI());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}