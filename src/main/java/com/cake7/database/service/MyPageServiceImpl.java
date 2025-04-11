package com.cake7.database.service;

import com.cake7.database.domain.UserSession;
import com.cake7.database.domain.Users;
import com.cake7.database.dto.MyPageRequestDTO;
import com.cake7.database.dto.MyPageResponseDTO;
import com.cake7.database.repository.UserRepository;
import com.cake7.database.repository.UserSessionRepository;
import com.cake7.database.util.UuidToBinary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.rmi.ServerException;
import java.util.Optional;
import java.util.UUID;

@Service
public class MyPageServiceImpl implements MyPageService{
    private final Logger logger = LoggerFactory.getLogger(MyPageServiceImpl.class.getName());
    private final UuidToBinary uuidToBinary;
    private final UserSessionRepository userSessionRepository;
    private final UserRepository userRepository;

    public MyPageServiceImpl(UuidToBinary uuidToBinary, UserSessionRepository userSessionRepository, UserRepository userRepository) {
        this.uuidToBinary = uuidToBinary;
        this.userSessionRepository = userSessionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public MyPageResponseDTO getMyPage(MyPageRequestDTO myPageRequestDTO) throws ServerException {
        UUID uuid = UUID.fromString(myPageRequestDTO.sessionId());
        byte[] binary = uuidToBinary.uuidToBytes(uuid);
        logger.debug("binary: {}", binary);
        try {
            Optional<UserSession> session = userSessionRepository.findById(binary);
            if (session.isPresent()) {
                UserSession getSession = session.get();
                byte[] userId = getSession.getUserId();
                Optional<Users> user = userRepository.findById(userId);
                if (user.isPresent()) {
                    Users getUser = user.get();
                    logger.debug("email: {} name: {}", getUser.getEmail(), getUser.getName());
                    return new MyPageResponseDTO(getUser.getEmail(), getUser.getName());
                }
            }
        } catch (Exception e) {
            logger.error("getMyPage error: {}", e.getMessage());
            throw new ServerException("getMyPage error: " + e.getMessage());
        }
        logger.error("user is null");
        return null;
    }
}
