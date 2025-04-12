package com.cake7.database.service;

import com.cake7.database.domain.Users;
import com.cake7.database.dto.MyPageResponseDTO;
import com.cake7.database.repository.UserRepository;
import com.cake7.database.util.Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.rmi.ServerException;
import java.util.Optional;
import java.util.UUID;

@Service
public class MyPageServiceImpl implements MyPageService{
    private static final Logger logger = LoggerFactory.getLogger(MyPageServiceImpl.class.getName());
    private final UserRepository userRepository;
    private final Convert convert;

    public MyPageServiceImpl(UserRepository userRepository, Convert convert) {
        this.convert = convert;
        this.userRepository = userRepository;
    }

    @Override
    public MyPageResponseDTO getMyPage(String sessionId) throws ServerException {
        UUID uuid = UUID.fromString(sessionId);
        byte[] binary = convert.uuidToBytes(uuid);
        logger.debug("binary: {}", binary);
        try {
                Optional<Users> user = userRepository.findWithUserById(binary);
                if (user.isPresent()) {
                    Users getUser = user.get();
                    logger.debug("email: {} name: {}", getUser.getEmail(), getUser.getName());
                    return new MyPageResponseDTO(getUser.getEmail(), getUser.getName());
                }
        } catch (Exception e) {
            logger.error("getMyPage error: {}", e.getMessage());
            throw new ServerException("getMyPage error: " + e.getMessage());
        }
        logger.error("user is null");
        return null;
    }
}
