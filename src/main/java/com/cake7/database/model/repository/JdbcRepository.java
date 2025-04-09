package com.cake7.database.model.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface JdbcRepository<T, ID> {
    Logger logger = LoggerFactory.getLogger(JdbcRepository.class);

    Map<String, Object> entityToMap(T entity);
    DataSource getDataSource();
    String getTableName();
    RowMapper<T> getRowMapper();

    default Optional<T> findById(ID id,  RowMapper<T> rowMapper) throws Exception {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";
        try(Connection conn = getDataSource().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rowMapper.mapRow(rs, 1));
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Exception: " + e.getMessage());
            throw new SQLException("쿼리 실행 중 오류 발생" +e.getMessage());

        } catch (DataAccessResourceFailureException e) {
            logger.error("DataAccessResourceFailureException: " + e.getMessage());
            throw new DataAccessResourceFailureException("데이터베이스 연결 또는 쿼리 실행 중 오류 발생" +e.getMessage());
        } catch (Exception e) {
            logger.error("Exception: " + e.getMessage());
            throw new Exception("예상하지 못한 오류" + e.getMessage());
        }
        return Optional.empty();
    }

    default void save(T entity) throws SQLException {
        Map<String, Object> columnValues = entityToMap(entity);
        if (columnValues.isEmpty()) {
            throw new IllegalArgumentException("Entity must have at least one column value");
        }

        String columns = String.join(", ", columnValues.keySet());
        String placeholders = columnValues.keySet().stream()
                .map(k -> "?")
                .collect(Collectors.joining(", "));

        String sql = "INSERT INTO " + getTableName() + " (" + columns + ") VALUES (" + placeholders + ")";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            for (Object value : columnValues.values()) {
                pstmt.setObject(paramIndex++, value);
            }

            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Exception: " + e.getMessage());
            throw new SQLException("쿼리 실행 중 오류 발생: " + e.getMessage());
        } catch (DataAccessResourceFailureException e) {
            logger.error("DataAccessResourceFailureException: " + e.getMessage());
            throw new DataAccessResourceFailureException("데이터베이스 연결 또는 쿼리 실행 중 오류 발생: " + e.getMessage());
        }
    }
}
