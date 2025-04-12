package com.cake7.database.controller;

import com.cake7.database.dto.SignInRequestDTO;
import com.cake7.database.service.SignInServiceImpl;
import com.cake7.database.util.Convert;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api", produces = "text/html; charset=UTF-8")  // 엔드포인트에 공통 URL이 있다면 추가됨
public class SignInController {
    private final SignInServiceImpl signInService;
    private final Convert convert;

    public SignInController(SignInServiceImpl signInService, Convert convert) {
        this.signInService = signInService;
        this.convert = convert;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<String> doPost(@RequestBody SignInRequestDTO signInRequestDTO, HttpServletResponse response, HttpSession httpSession) {
        try {
            UUID sessionId = signInService.signIn(signInRequestDTO);
            if (Objects.nonNull(sessionId)) {

                Cookie cookie = new Cookie("SESSION_ID", sessionId.toString());
                cookie.setHttpOnly(true);        // 자바스크립트에서 접근 불가
                cookie.setPath("/");             // 전체 경로에 적용
                cookie.setMaxAge(7 * 24 * 60 * 60);  // 일주일 유효
                // cookie.setSecure(true); // HTTPS 쓸 땐 켜기
                response.addCookie(cookie);
                httpSession.setAttribute("SESSION_ID", sessionId.toString());
                return ResponseEntity.status(HttpStatus.OK).body("로그인 성공");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("아이디 또는 비밀번호가 잘못되었습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 에러");
        }
    }
}
