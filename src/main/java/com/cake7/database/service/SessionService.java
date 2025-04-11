package com.cake7.database.service;

import com.cake7.database.domain.UserSession;

import java.util.Optional;

public interface SessionService {
    boolean isValid(String sessionId);
    Optional<String> getSessionUser(String sessionId);
    void createSession(String sessionId, UserSession user);
    void removeSession(String sessionId);
}
