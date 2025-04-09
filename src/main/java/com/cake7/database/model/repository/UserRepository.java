    package com.cake7.database.model.repository;

    import com.cake7.database.domain.Users;
    import org.springframework.dao.DataAccessResourceFailureException;
    import org.springframework.jdbc.core.RowMapper;
    import org.springframework.stereotype.Repository;

    import javax.sql.DataSource;
    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    import java.sql.SQLException;
    import java.util.Map;
    import java.util.Optional;

    @Repository
    public class UserRepository implements JdbcRepository<Users, byte[]> {

        private final DataSource dataSource;

        public UserRepository(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public DataSource getDataSource() {
            return dataSource;
        }

        @Override
        public String getTableName() {
            return "users";
        }

        @Override
        public RowMapper<Users> rowMapper() {
            return (rs, rowNum) -> new Users(rs.getBytes("id"),
                    rs.getString("email"),
                    rs.getString("name"),
                    rs.getString("password"),
                    rs.getString("salt")); // 이게 빠지면 null 나와
        }


        public boolean existByEmail(String email) throws SQLException {
            String sql = "SELECT count(*) FROM " + getTableName() + " WHERE email = ? LIMIT 1";
            try(Connection conn = getDataSource().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setObject(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            } catch (SQLException e) {
                logger.error("SQL Exception: " + e.getMessage());
                throw new SQLException("쿼리 실행 중 오류 발생" +e.getMessage());

            } catch (DataAccessResourceFailureException e) {
                logger.error("DataAccessResourceFailureException: " + e.getMessage());
                throw new DataAccessResourceFailureException("데이터베이스 연결 또는 쿼리 실행 중 오류 발생" +e.getMessage());
            }
            return false;
        }

        public Optional<Users> findByEmail(String email, RowMapper<Users> rowMapper) throws SQLException {
            String sql = "SELECT * FROM " + getTableName() + " WHERE email = ?";
            try(Connection conn = getDataSource().getConnection()) {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(rowMapper.mapRow(rs, 1));
                    }
                }
            } catch (SQLException e) {
                logger.error("SQL Exception: " + e.getMessage());
                throw new SQLException("SQL Exception: " + e.getMessage());
            } catch (DataAccessResourceFailureException e) {
                logger.error("DataAccessResourceFailureException: " + e.getMessage());
                throw new DataAccessResourceFailureException("SQL Exception: " + e.getMessage());
            }
            return Optional.empty();
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
