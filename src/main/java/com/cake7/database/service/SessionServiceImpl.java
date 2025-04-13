package com.cake7.database.service;

import com.cake7.database.repository.UserSessionRepository;
import com.cake7.database.util.Convert;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SessionServiceImpl implements SessionService {
    private final UserSessionRepository userSessionRepository;
    private final Convert convert;
    private static final Logger logger = LoggerFactory.getLogger(SessionServiceImpl.class.getName());
    private final Cache<String, Boolean> cache = Caffeine.newBuilder()
                                                    .expireAfterWrite(30, TimeUnit.MINUTES)
                                                    .maximumSize(100000)
                                                    .build();

    public SessionServiceImpl(UserSessionRepository userSessionRepository, Convert convert) {
        this.userSessionRepository = userSessionRepository;
        this.convert = convert;
    }


    @Override
    public boolean isValid(String sessionId) {
        if (cache.getIfPresent(sessionId) != null) {
            logger.debug("Session [{}] found in cache", sessionId);
            return true;
        }

        byte[] session = convert.uuidToBytes(UUID.fromString(sessionId));


        return userSessionRepository.findById(session).map(
                entity ->  {
                    cache.put(sessionId, true);
                    logger.debug("Session [{}] found in DB and cached", sessionId);
                    return true;}
                ).orElseGet( () -> {
                    logger.debug("Session [{}] not found in DB", sessionId);
                    return false;
                }
                );
    }
}
