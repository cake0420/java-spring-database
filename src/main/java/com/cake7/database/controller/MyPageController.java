package com.cake7.database.controller;

import com.cake7.database.dto.MyPageResponseDTO;
import com.cake7.database.service.MyPageServiceImpl;
import com.cake7.database.service.SessionServiceImpl;
import com.cake7.database.util.Convert;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/protected", produces = "application/json")
public class MyPageController {
    private final MyPageServiceImpl myPageServiceImpl;
    private final SessionServiceImpl sessionServiceImpl;
    private final Convert convert;

    public MyPageController(MyPageServiceImpl myPageServiceImpl, SessionServiceImpl sessionServiceImpl, Convert convert) {
        this.myPageServiceImpl = myPageServiceImpl;
        this.sessionServiceImpl = sessionServiceImpl;
        this.convert = convert;
    }

    @GetMapping("/mypage")
    public ResponseEntity<MyPageResponseDTO> getMyPage(@CookieValue("SESSION_ID") String sessionId) {
        if (sessionId == null || !sessionServiceImpl.isValid(sessionId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {

            MyPageResponseDTO myPageResponseDTO = myPageServiceImpl.getMyPage(sessionId);
            return ResponseEntity.ok(myPageResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
