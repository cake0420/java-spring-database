package com.cake7.database.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record UserSessionDTO(LocalDateTime lastAccessedAt, LocalDateTime expiresAt) implements Serializable {
}
