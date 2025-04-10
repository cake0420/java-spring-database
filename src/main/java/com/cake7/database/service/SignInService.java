package com.cake7.database.service;

import com.cake7.database.dto.SignInRequestDTO;

import java.rmi.ServerException;
import java.sql.SQLException;
import java.util.UUID;

public interface SignInService {
    UUID signIn(SignInRequestDTO signInRequestDTO) throws SQLException, ServerException;
}
