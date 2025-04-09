package com.cake7.database.service;

import com.cake7.database.domain.Users;

import java.rmi.ServerException;
import java.sql.SQLException;
import java.util.Optional;

public interface SignInService {
    Optional<Users> signIn(String email, String password) throws SQLException, ServerException;
}
