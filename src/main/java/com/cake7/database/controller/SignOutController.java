package com.cake7.database.controller;

import com.cake7.database.dto.SignOutRequestDTO;
import com.cake7.database.service.SignOutServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api", produces = "text/html; charset=UTF-8")  // 엔드포인트에 공통 URL이 있다면 추가됨
public class SignOutController {
    private final SignOutServiceImpl signOutServiceImpl;

    public SignOutController(SignOutServiceImpl signOutServiceImpl) {
        this.signOutServiceImpl = signOutServiceImpl;
    }

    @PostMapping("/sign-out")
    public ResponseEntity<String> doPost(@RequestBody SignOutRequestDTO signOutRequestDTO) {
        try {
            boolean result = signOutServiceImpl.signOut(signOutRequestDTO);
            return result ? ResponseEntity.ok("정상적으로 로그아웃 됐습니다")
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저 정보를 찾을 수 없습니다");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러");
        }
    }
}
