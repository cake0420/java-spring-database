package com.cake7.database.model.repository;

import com.cake7.database.domain.UserSession;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class UserSessionRepository implements JdbcRepository<UserSession, byte[]>{
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<UserSession> getRowMapper = (rs, rowNum) ->
            new UserSession(
                    rs.getBytes("sessionId"),
                    rs.getBytes("userId"),
                    rs.getString("ipAddress"),
                    rs.getString("userAgent"),
                    rs.getTimestamp("createdAt").toLocalDateTime(),
                    rs.getTimestamp("lastAccessedAt").toLocalDateTime(),
                    rs.getTimestamp("expiresAt").toLocalDateTime(),
                    rs.getBoolean("isValid")
            );


    public UserSessionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<String, Object> entityToMap(UserSession entity) {
        return Map.of(
                "session_id", entity.getSessionId(),
                "user_id", entity.getUserId(),
                "ip_address", entity.getIpAddress(),
                "user_agent", entity.getUserAgent(),
                "created_at", entity.getCreatedAt(),
                "last_accessed_at", entity.getLastAccessedAt(),
                "expired_at", entity.getExpiresAt(),
                "is_valid", entity.isValid()
        );
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    public String getTableName() {
        return "user_sessions";
    }

    @Override
    public RowMapper<UserSession> getRowMapper() {
        return getRowMapper;
    }
}
