package com.cake7.database.service;

import java.util.Optional;

public interface SessionService {
    boolean isValid(String sessionId);
    Optional<String> getSessionUser(String sessionId);
}
