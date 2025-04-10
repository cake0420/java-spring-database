package com.cake7.database.service;

import com.cake7.database.dto.SignOutRequestDTO;

public interface SignOutService {
    boolean signOut(SignOutRequestDTO signOutRequestDTO) throws Exception;
}
