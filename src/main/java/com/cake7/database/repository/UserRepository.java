    package com.cake7.database.repository;

    import com.cake7.database.domain.Users;
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
    public class UserRepository implements JdbcRepository<Users, byte[]> {
        private final Logger logger = LoggerFactory.getLogger(UserRepository.class.getName());
        private final JdbcTemplate jdbcTemplate;
        private final RowMapper<Users> getRowMapper = (rs, rowNum)
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
            return getRowMapper;
        }

        @Override
        public JdbcTemplate getJdbcTemplate() {
            return jdbcTemplate;
        }


        public boolean existByEmail(String email) throws ServerException {
            String sql = """
                        SELECT count(*) FROM %s WHERE email = ? LIMIT 1
                    """.formatted(getTableName());
            try {
                Integer count = getJdbcTemplate().queryForObject(sql, Integer.class, email);
                return count != null && count > 0;
            } catch (Exception e) {
                logger.error("Error checking if email exists: {}", e.getMessage());
                throw new ServerException("server error: " + e.getMessage());
            }
        }

        public Optional<Users> findByEmail(String email) throws ServerException {
            String sql = """
                    SELECT * FROM %s WHERE email = ?
                """.formatted(getTableName());
            try {
                Users user = getJdbcTemplate().queryForObject(sql, getRowMapper(), email);
                return Optional.ofNullable(user);
            }  catch (EmptyResultDataAccessException e) {
                return Optional.empty();
            } catch (Exception e) {
                logger.error("Error finding by email: {}",e.getMessage());
                throw new ServerException("server error: "+ e.getMessage());
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

        public Optional<Users> findWithUserById(byte[] id) throws ServerException {
            String sql = """
                    SELECT u.*
                    FROM user_sessions us
                    INNER JOIN %s u ON us.user_id = u.id
                    WHERE us.id = ?
                """.formatted(getTableName());
            try {
                Users user = getJdbcTemplate().queryForObject(sql, getRowMapper(), id);
                return Optional.ofNullable(user);
            } catch (EmptyResultDataAccessException e) {
                return Optional.empty();
            } catch (Exception e) {
                logger.error("Error finding left join user session with user id: {}",e.getMessage());
                throw new ServerException("server error: "+ e.getMessage());
            }
        }

    }
