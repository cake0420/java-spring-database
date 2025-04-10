package com.cake7.database.controller;

import com.cake7.database.dto.UserSessionDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

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
        UserSessionDTO sessionDTO = (UserSessionDTO) session.getAttribute("SESSION_ID");
        if (sessionDTO == null) {
            return "redirect:/signin";
        }
        return "mypage";
    }
}