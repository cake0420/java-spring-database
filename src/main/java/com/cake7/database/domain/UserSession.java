package com.cake7.database.domain;

import java.time.LocalDateTime;

public class UserSession {

    private final byte[] id;
    private final byte[] userId;
    private final String ipAddress;
    private final String userAgent;
    private final LocalDateTime createdAt;
    private final LocalDateTime lastAccessedAt;
    private final LocalDateTime expiresAt;
    private final boolean isValid;

    public UserSession(byte[] id, byte[] userId, String ipAddress,
                       String userAgent, LocalDateTime createdAt,
                       LocalDateTime lastAccessedAt, LocalDateTime expiresAt, boolean isValid) {
        this.id = id;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = createdAt;
        this.lastAccessedAt = lastAccessedAt;
        this.expiresAt = expiresAt;
        this.isValid = isValid;
    }

    public byte[] getSessionId() {
        return id;
    }
    public byte[] getUserId() {
        return userId;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public String getUserAgent() {
        return userAgent;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
    }
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    public boolean isValid() {
        return isValid;
    }
}
