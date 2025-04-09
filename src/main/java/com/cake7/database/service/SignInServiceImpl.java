package com.cake7.database.service;

import com.cake7.database.domain.Users;
import com.cake7.database.model.repository.UserRepository;
import com.cake7.database.util.Encrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;

import java.rmi.ServerException;
import java.sql.SQLException;
import java.util.Optional;


@Service
public class SignInServiceImpl implements SignInService {
    Logger logger = LoggerFactory.getLogger(SignInServiceImpl.class);
    private final UserRepository userRepository;
    private final Encrypt encrypt;
    public SignInServiceImpl(UserRepository userRepository, Encrypt encrypt) {
        this.userRepository = userRepository;
        this.encrypt = encrypt;
    }

    public Optional<Users> signIn(String email, String password) throws SQLException, ServerException {
        try {
            Optional<Users> user =  userRepository.findByEmail(email, userRepository.rowMapper());
            if (user.isPresent()) {
                String encryptPassword = encrypt.getEncrypt(password, user.get().getSalt());
                if(user.get().getPassword().equals(encryptPassword + user.get().getSalt())) {
                    logger.debug(user.get().getEmail());
                    return user;
                }
            }
            return Optional.empty();
        }
        catch (SQLException e) {
            logger.error("SQL Exception: " + e.getMessage());
            throw new SQLException("쿼리 실행 중 오류 발생" +e.getMessage());

        } catch (DataAccessResourceFailureException e) {
            logger.error("DataAccessResourceFailureException: " + e.getMessage());
            throw new DataAccessResourceFailureException("데이터베이스 연결 또는 쿼리 실행 중 오류 발생" +e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }
}
