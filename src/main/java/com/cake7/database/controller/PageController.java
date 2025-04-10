package com.cake7.database.controller;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("charset=UTF-8")
public class PageController {
    private final Logger logger = LoggerFactory.getLogger(PageController.class.getName());
    @GetMapping("/")
    public String index() {
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
    public String mypage(HttpSession session) {
        String sessionDTO = (String) session.getAttribute("SESSION_ID");
        if (sessionDTO == null) {
            return "redirect:/signin";
        }
        return "mypage";
    }
}