package com.cake7.database.interceptor;

import com.cake7.database.dto.UserSessionDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthenticationInterceptor implements HandlerInterceptor {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        // 세션이 없거나 세션에 SESSION_ID 속성이 없으면 인증 실패
        if (session == null || session.getAttribute("SESSION_ID") == null) {
            logger.debug("Unauthorized access attempt to {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("로그인이 필요합니다.");
            return false;
        }

        // 세션이 있고 SESSION_ID가 있으면 인증 성공
        UserSessionDTO userSessionDTO = (UserSessionDTO) session.getAttribute("SESSION_ID");
        logger.debug("Authenticated user accessing {}", request.getRequestURI());
        return true;
    }
}