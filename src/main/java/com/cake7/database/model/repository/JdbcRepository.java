package com.cake7.database.model.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import java.util.Map;
import java.util.Optional;

public interface JdbcRepository<T, ID> {
    Logger logger = LoggerFactory.getLogger(JdbcRepository.class);

    Map<String, Object> entityToMap(T entity);
    JdbcOperations getJdbcTemplate();
    String getTableName();
    RowMapper<T> getRowMapper();

    default Optional<T> findById(ID id) throws Exception {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";
        try {
            return Optional.ofNullable(getJdbcTemplate().queryForObject(sql, getRowMapper(), id));
        } catch (Exception e) {
            logger.error("Error finding by ID: " + e.getMessage());
            return Optional.empty();
        }
    }


    default void save(T entity) {
        Map<String, Object> columnValues = entityToMap(entity);
        if (columnValues.isEmpty()) {
            throw new IllegalArgumentException("Entity must have at least one column value");
        }

        String columns = String.join(", ", columnValues.keySet());
        String placeholders = String.join(", ", columnValues.keySet().stream()
                .map(k -> "?")
                .toList());

        String sql = "INSERT INTO " + getTableName() + " (" + columns + ") VALUES (" + placeholders + ")";

        Object[] values = columnValues.values().toArray();

        try {
            getJdbcTemplate().update(sql, values);
        } catch (Exception e) {
            logger.error("Error saving entity: " + e.getMessage());
            throw e;
        }
    }
}

