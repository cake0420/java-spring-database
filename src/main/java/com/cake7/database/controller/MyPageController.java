package com.cake7.database.controller;

import com.cake7.database.dto.MyPageRequestDTO;
import com.cake7.database.dto.MyPageResponseDTO;
import com.cake7.database.service.MyPageServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.rmi.ServerException;

@RestController
@RequestMapping(value = "/api/protected", produces = "application/json")
public class MyPageController {
    private final MyPageServiceImpl myPageServiceImpl;

    public MyPageController(MyPageServiceImpl myPageServiceImpl) {
        this.myPageServiceImpl = myPageServiceImpl;
    }

    @PostMapping("/mypage")
    public ResponseEntity<MyPageResponseDTO> doPost(@CookieValue("SESSION_ID") String sessionId,
                                                    @RequestBody MyPageRequestDTO myPageRequestDTO) throws ServerException {
        MyPageResponseDTO myPageResponseDTO = myPageServiceImpl.getMyPage(myPageRequestDTO);
        try {
            return ResponseEntity.ok(myPageResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
