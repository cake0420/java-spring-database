package com.cake7.database.controller;

import com.cake7.database.service.SessionServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("charset=UTF-8")
public class PageController {
    private final Logger logger = LoggerFactory.getLogger(PageController.class.getName());
    private final SessionServiceImpl sessionService;

    public PageController(SessionServiceImpl sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/")
    public String index(@CookieValue(value = "SESSION_ID", required = false) String sessionId) {
        return "index";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/signin")
    public String signin() {
        return "signin";
    }

    @GetMapping("/mypage")
    public String mypage(@CookieValue(value = "SESSION_ID", required = false) String sessionId) {
        if (sessionId == null || !sessionService.isValid(sessionId)) {
            return "redirect:/signin";
        }
        return "mypage";
    }
}