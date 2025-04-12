package com.cake7.database.service;

import com.cake7.database.dto.MyPageResponseDTO;

import java.rmi.ServerException;

public interface MyPageService {
    MyPageResponseDTO getMyPage(String sessionId) throws ServerException;
}

