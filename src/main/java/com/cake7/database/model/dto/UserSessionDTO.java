package com.cake7.database.model.dto;

import java.io.Serializable;

public record UserSessionDTO(byte[] userId, String email, String name) implements Serializable {
}
