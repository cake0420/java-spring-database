package com.cake7.database.service;

import com.cake7.database.dto.MyPageRequestDTO;
import com.cake7.database.dto.MyPageResponseDTO;

import java.rmi.ServerException;

public interface MyPageService {
    MyPageResponseDTO getMyPage(MyPageRequestDTO myPageRequestDTO) throws ServerException;
}

