package com.cake7.database.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

public class CsrfInterceptor implements HandlerInterceptor {
    private final Logger logger = LoggerFactory.getLogger(CsrfInterceptor.class.getName());
    private static final List<String> ALLOWED_ORIGINS = List.of("https://java-spring-database.onrender.com",
                                                                "http://localhost:8080",
                                                                "https://eastern-rowena-jack6767-df59f302.koyeb.app");
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        if (method.equals("GET")) return true;

        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");

        // Origin 또는 Referer가 둘 다 없으면 의심
        if (origin == null && referer == null) {
            logger.warn("CSRF 차단 - Origin 및 Referer 없음: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        // origin 또는 referer가 허용되지 않은 경우 차단
        if ((origin != null && !ALLOWED_ORIGINS.contains(origin)) ||
                (referer != null && ALLOWED_ORIGINS.stream().noneMatch(referer::startsWith))) {
            logger.warn("CSRF 차단 - Origin 또는 Referer 불일치: Origin={}, Referer={}", origin, referer);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        return true;
    }
}
