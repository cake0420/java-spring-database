package com.cake7.database.repository;

import com.cake7.database.domain.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.rmi.ServerException;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserSessionRepository implements JdbcRepository<UserSession, byte[]>{
    private static final Logger logger = LoggerFactory.getLogger(UserSessionRepository.class.getName());
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<UserSession> getRowMapper = (rs, rowNum) ->
            new UserSession(
                    rs.getBytes("id"),
                    rs.getBytes("user_id"),
                    rs.getString("ip_address"),
                    rs.getString("user_agent"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("last_accessed_at").toLocalDateTime(),
                    rs.getTimestamp("expired_at").toLocalDateTime(),
                    rs.getBoolean("is_valid")
            );


    public UserSessionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<String, Object> entityToMap(UserSession entity) {
        return Map.of(
                "id", entity.getSessionId(),
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

    public int deleteBySessionId(byte[] sessionId) throws ServerException {
        String sql = """
                    DELETE FROM %s WHERE id = ?
                """.formatted(getTableName());
        try {
            return getJdbcTemplate().update(sql, sessionId);
        } catch (Exception e) {
            logger.error("Error checking if user id exists: {}", e.getMessage());
            throw new ServerException("server error: " + e.getMessage());
        }
    }

    @Override
    public UserSession save(UserSession entity) {
        Map<String, Object> columnValues = entityToMap(entity);
        if (columnValues.isEmpty()) {
            throw new IllegalArgumentException("Entity must have at least one column value");
        }

        String checkSql = """
        SELECT CASE\s
            WHEN EXISTS (
                SELECT 1 FROM %s\s
                WHERE user_id = ? AND ip_address = ? AND user_agent = ? AND is_valid = true
            ) THEN 1\s
            ELSE 0\s
        END
       \s""".formatted(getTableName());

        try {
            boolean exists = Boolean.TRUE.equals(getJdbcTemplate().queryForObject(
                    checkSql, Boolean.class,
                    entity.getUserId(), entity.getIpAddress(), entity.getUserAgent()
            ));

            if (exists) {
                String updateSql = """
                UPDATE %s\s
                SET last_accessed_at = NOW(),\s
                    expired_at = DATE_ADD(NOW(), INTERVAL 7 DAY)\s
                WHERE user_id = ? AND ip_address = ? AND user_agent = ? AND is_valid = true
                LIMIT 1
           \s""".formatted(getTableName());
                getJdbcTemplate().update(updateSql,
                        entity.getUserId(), entity.getIpAddress(), entity.getUserAgent());
                String selectSql = """
                            SELECT * FROM %s\s
                            WHERE user_id = ? AND ip_address = ? AND user_agent = ? AND is_valid = true
                            LIMIT 1
                        """.formatted(getTableName());
                return getJdbcTemplate().queryForObject(selectSql, getRowMapper(),
                        entity.getUserId(), entity.getIpAddress(), entity.getUserAgent());
            } else {
                String columns = String.join(", ", columnValues.keySet());
                String placeholders = String.join(", ", columnValues.keySet().stream().map(k -> "?").toList());
                String insertSql = """
                    INSERT INTO %s (%s) VALUES (%s)
                """.formatted(getTableName(), columns, placeholders);
                Object[] values = columnValues.values().toArray();
                getJdbcTemplate().update(insertSql, values);
                return entity;
            }
        } catch (Exception e) {
            logger.error("Error saving user session: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save user session", e);
        }
    }

    public int deleteExpiredSessions() throws ServerException {
        String sql = """
                    DELETE FROM %s WHERE expired_at < NOW() OR is_valid = false
                """.formatted(getTableName());
        try {
            return getJdbcTemplate().update(sql);
        } catch (Exception e) {
            logger.error("Error deleting expired sessions: {}", e.getMessage(), e);
            throw new ServerException("Error deleting expired sessions: " + e.getMessage());
        }
    }

    public boolean existsByIdAndValid(byte[] sessionId, boolean valid) throws ServerException {
        String sql = """
                SELECT EXISTS (SELECT 1 FROM %s WHERE id = ? AND is_valid = ?)
            """.formatted(getTableName());
        try {
            return Boolean.TRUE.equals(getJdbcTemplate().queryForObject(sql, Boolean.class, sessionId, valid));
        } catch (Exception e) {
            logger.error("Error checking if session id exists: {}", e.getMessage());
            throw new ServerException("server error: " + e.getMessage());
        }
    }

    public Optional<UserSession> findBySessionIdWithUserId(String email) throws ServerException {
        String sql = """
                    SELECT *\s
                    FROM %s us
                    INNER JOIN users u ON us.user_id = u.id
                    WHERE u.email = ?;
                """.formatted(getTableName());
        try {
            UserSession user = getJdbcTemplate().queryForObject(sql, getRowMapper(), email);
            return Optional.ofNullable(user);
        }  catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error finding session for email {}: {}", email, e.getMessage());
            throw new ServerException("server error: "+ e.getMessage());
        }
    }
}
