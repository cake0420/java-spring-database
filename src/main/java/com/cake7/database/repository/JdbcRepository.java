package com.cake7.database.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.rmi.ServerException;
import java.util.Map;
import java.util.Optional;

public interface JdbcRepository<T, ID> {
    Map<String, Object> entityToMap(T entity);
    JdbcTemplate getJdbcTemplate();
    String getTableName();
    RowMapper<T> getRowMapper();

    default Optional<T> findById(ID id) {
        String sql = """
            SELECT * FROM %s WHERE id = ?
        """.formatted(getTableName());
        try {
            T result = getJdbcTemplate().queryForObject(sql, getRowMapper(), id);
            return Optional.ofNullable(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    default T save(T entity) throws ServerException {
        Map<String, Object> columnValues = entityToMap(entity);
        if (columnValues.isEmpty()) {
            throw new IllegalArgumentException("Entity must have at least one column value");
        }

        String columns = String.join(", ", columnValues.keySet());
        String placeholders = String.join(", ", columnValues.keySet().stream()
                .map(k -> "?")
                .toList());

        String sql =  """
                    INSERT INTO %s (%s) VALUES (%s)
                """.formatted(getTableName(), columns, placeholders);

        Object[] values = columnValues.values().toArray();

        try {
            getJdbcTemplate().update(sql, values);
            return entity;
        } catch (Exception e) {
            throw new ServerException("server error " + e.getMessage());
        }
    }
}

