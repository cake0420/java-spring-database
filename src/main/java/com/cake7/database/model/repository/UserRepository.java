    package com.cake7.database.model.repository;

    import com.cake7.database.domain.Users;
    import org.springframework.dao.EmptyResultDataAccessException;
    import org.springframework.jdbc.core.JdbcOperations;
    import org.springframework.jdbc.core.JdbcTemplate;
    import org.springframework.jdbc.core.RowMapper;
    import org.springframework.stereotype.Repository;

    import javax.sql.rowset.serial.SerialException;
    import java.rmi.ServerException;
    import java.sql.SQLException;
    import java.util.Map;
    import java.util.Optional;

    @Repository
    public class UserRepository implements JdbcRepository<Users, byte[]> {

        private final JdbcTemplate jdbcTemplate;
        private final RowMapper<Users> rowMapper = (rs, rowNum)
                -> new Users(
                    rs.getBytes("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("salt")
                );

        public UserRepository(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }

        @Override
        public String getTableName() {
            return "users";
        }

        @Override
        public RowMapper<Users> getRowMapper() {
            return null;
        }

        @Override
        public JdbcOperations getJdbcTemplate() {
            return null;
        }

        public boolean existByEmail(String email) throws SQLException {
            String sql = "SELECT count(*) FROM " + getTableName() + " WHERE email = ? LIMIT 1";
            try {
                Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
                return count != null && count > 0;
            } catch (Exception e) {
                logger.error("Error checking if email exists: " + e.getMessage());
                throw new SerialException("server error: " + e.getMessage());
            }
        }

        public Optional<Users> findByEmail(String email) throws ServerException {
            String sql = "SELECT * FROM " + getTableName() + " WHERE email = ?";
            try {
                Users user = jdbcTemplate.queryForObject(sql, rowMapper, email);
                return Optional.ofNullable(user);
            } catch (EmptyResultDataAccessException e) {
                throw new EmptyResultDataAccessException("User with email '" + email + "' not found", 1);
            } catch (Exception e) {
                logger.error("Error finding by email: " + e.getMessage());
                throw new ServerException("server error: " + e.getMessage());
            }
        }

        @Override
        public Map<String, Object> entityToMap(Users user) {
            return Map.of(
                    "id", user.getId(),
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "password", user.getPassword(),
                    "salt", user.getSalt()
            );
        }

    }
