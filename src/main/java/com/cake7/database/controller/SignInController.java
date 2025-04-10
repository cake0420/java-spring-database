package com.cake7.database.controller;

import com.cake7.database.model.dto.SignInRequestDTO;
import com.cake7.database.model.dto.UserSessionDTO;
import com.cake7.database.service.SignInServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping(value = "/api", produces = "text/html; charset=UTF-8")  // 엔드포인트에 공통 URL이 있다면 추가됨
public class SignInController {
    private final SignInServiceImpl signInService;
    public SignInController(SignInServiceImpl signInService) {
        this.signInService = signInService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> doPost(@RequestBody SignInRequestDTO signInRequestDTO, HttpSession httpSession) {
        try {
            byte[] sessionId = signInService.signIn(signInRequestDTO);
            if (Objects.nonNull(sessionId)) {
                UserSessionDTO userSessionDTO = new UserSessionDTO(sessionId);

                httpSession.setAttribute("SESSION_ID", userSessionDTO);
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
