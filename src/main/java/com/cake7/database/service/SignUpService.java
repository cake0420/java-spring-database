package com.cake7.database.service;

import com.cake7.database.domain.Users;
import com.cake7.database.dto.SignUpRequestDTO;

public interface SignUpService {

    Users signUp(SignUpRequestDTO signUpRequestDTO) throws  Exception;
}
