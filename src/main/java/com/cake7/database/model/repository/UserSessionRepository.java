package com.cake7.database.model.repository;

import com.cake7.database.domain.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.rmi.ServerException;
import java.util.Map;

@Repository
public class UserSessionRepository implements JdbcRepository<UserSession, byte[]>{
    private final Logger logger = LoggerFactory.getLogger(UserSessionRepository.class.getName());
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

    public boolean existByUserId(byte[] userId) throws ServerException {
        String sql = "SELECT count(*) FROM " + getTableName() + " WHERE user_id = ? LIMIT 1";
        try {
            Integer count = getJdbcTemplate().queryForObject(sql, Integer.class, (Object) userId);
            return count != null && count > 0;
        } catch (Exception e) {
            logger.error("Error checking if user id exists: {}", e.getMessage());
            throw new ServerException("server error: " + e.getMessage());
        }
    }

    @Override
    public void save(UserSession entity) {
        Map<String, Object> columnValues = entityToMap(entity);
        if (columnValues.isEmpty()) {
            throw new IllegalArgumentException("Entity must have at least one column value");
        }

        String checkSql = """
        SELECT CASE 
            WHEN EXISTS (
                SELECT 1 FROM user_sessions 
                WHERE user_id = ? AND ip_address = ? AND user_agent = ? AND is_valid = true
            ) THEN 1 
            ELSE 0 
        END
        """;

        try {
            boolean exists = Boolean.TRUE.equals(getJdbcTemplate().queryForObject(
                    checkSql, Boolean.class,
                    entity.getUserId(), entity.getIpAddress(), entity.getUserAgent()
            ));

            if (exists) {
                String updateSql = """
                UPDATE user_sessions 
                SET last_accessed_at = NOW(), 
                    expired_at = DATE_ADD(NOW(), INTERVAL 7 DAY) 
                WHERE user_id = ? AND ip_address = ? AND user_agent = ? AND is_valid = true
            """;
                getJdbcTemplate().update(updateSql,
                        entity.getUserId(), entity.getIpAddress(), entity.getUserAgent());
            } else {
                String columns = String.join(", ", columnValues.keySet());
                String placeholders = String.join(", ", columnValues.keySet().stream().map(k -> "?").toList());
                String insertSql = "INSERT INTO " + getTableName() + " (" + columns + ") VALUES (" + placeholders + ")";
                Object[] values = columnValues.values().toArray();
                getJdbcTemplate().update(insertSql, values);
            }
        } catch (Exception e) {
            logger.error("Error saving user session: " + e.getMessage(), e);
            throw new RuntimeException("Failed to save user session", e);
        }
    }

}
