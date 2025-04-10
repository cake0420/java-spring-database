package com.cake7.database.service;

import com.cake7.database.domain.Users;
import com.cake7.database.dto.SignUpRequestDTO;
import com.cake7.database.repository.UserRepository;
import com.cake7.database.util.Encrypt;
import com.cake7.database.util.UuidToBinary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.ServerException;
import java.util.UUID;

@Service
@Transactional
public class SignUpServiceImpl implements SignUpService {
    private final Logger logger = LoggerFactory.getLogger(SignUpServiceImpl.class.getName());

    private final UserRepository userRepository;
    private final Encrypt encrypt;
    private final UuidToBinary uuidToBinary;

    public SignUpServiceImpl(UserRepository userRepository, Encrypt encrypt, UuidToBinary uuidToBinary) {
        this.userRepository = userRepository;
        this.encrypt = encrypt;
        this.uuidToBinary = uuidToBinary;
    }
    
    @Override
    public Users signUp(SignUpRequestDTO signUpRequestDTO) throws Exception {
        if (userRepository.existByEmail(signUpRequestDTO.email())) {
            throw new DuplicateKeyException("이미 존재하는 이메일입니다.");
            }
        try {
                String salt = encrypt.generateSalt();
                String newPassword = encrypt.getEncrypt(signUpRequestDTO.password(), salt);
                UUID uuid = UUID.randomUUID();
                byte[] binary_uuid = uuidToBinary.uuidToBytes(uuid);
                // UUID를 포함한 Users 객체 생성
                Users newUser = new Users(
                        binary_uuid,
                        signUpRequestDTO.name(),
                        signUpRequestDTO.email(),
                        newPassword,
                        salt
                );

                logger.debug(newUser.toString());
                userRepository.save(newUser);
                return newUser;

        } catch (DuplicateKeyException e) {
            logger.error("Duplicate key: {}",e.getMessage());
            throw new DuplicateKeyException("Duplicate key: " + e.getMessage());
        } catch (DataAccessResourceFailureException e) {
            logger.error("Database connection error: {}", e.getMessage());
            throw new DataAccessResourceFailureException("Database connection error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error during sign up: {}", e.getMessage());
            throw new ServerException("Error during sign up " + e.getMessage());
        }
    }
}
