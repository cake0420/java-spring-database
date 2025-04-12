package com.cake7.database.service;

import com.cake7.database.dto.SignOutRequestDTO;
import com.cake7.database.repository.UserSessionRepository;
import com.cake7.database.util.Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.ServerException;
import java.util.Arrays;
import java.util.UUID;

@Service
@Transactional
public class SignOutServiceImpl implements SignOutService {
    private static final Logger logger = LoggerFactory.getLogger(SignOutServiceImpl.class.getName());
    private final UserSessionRepository userSessionRepository;
    private final Convert convert;

    public SignOutServiceImpl(UserSessionRepository userSessionRepository, Convert convert) {
        this.userSessionRepository = userSessionRepository;
        this.convert = convert;
    }

    @Override
    public boolean signOut(SignOutRequestDTO signOutRequestDTO) throws Exception {
        UUID uuid = UUID.fromString(signOutRequestDTO.sessionId());
        byte[] binary = convert.uuidToBytes(uuid);
        logger.debug(uuid.toString());
        try {
                int count = userSessionRepository.deleteBySessionId(binary);
                logger.debug("delete user id: {}", Arrays.toString(binary));
                return count > 0;
        } catch (IllegalArgumentException e) {
            logger.error("bad sign out request: {}", e.getMessage());
            throw new IllegalArgumentException("bad sign out request");
        }
        catch (Exception e) {
            logger.error("Error during SignOut: {}", e.getMessage());
            throw new ServerException("Error during SignOut: " + e.getMessage());
        }

    }
}
