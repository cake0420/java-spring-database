package com.cake7.database.service;

import com.cake7.database.dto.SignInRequestDTO;

import java.rmi.ServerException;
import java.sql.SQLException;

public interface SignInService {
    byte[] signIn(SignInRequestDTO signInRequestDTO) throws SQLException, ServerException;
}
