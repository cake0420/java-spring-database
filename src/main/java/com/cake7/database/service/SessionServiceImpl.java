package com.cake7.database.service;

import com.cake7.database.domain.UserSession;
import com.cake7.database.repository.UserSessionRepository;
import com.cake7.database.util.Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionServiceImpl implements SessionService {
    private final UserSessionRepository userSessionRepository;
    private final Map<String, String> cache = new ConcurrentHashMap<>();
    private final Convert convert;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public SessionServiceImpl(UserSessionRepository userSessionRepository, Convert convert) {
        this.userSessionRepository = userSessionRepository;
        this.convert = convert;
    }


    @Override
    public boolean isValid(String sessionId) {
        if (cache.containsKey(sessionId)) {
            logger.debug("Session [{}] found in cache", sessionId);
            return true;
        }

        byte[] session = convert.uuidToBytes(UUID.fromString(sessionId));


        return userSessionRepository.findById(session).map(
                entity ->  {
                    cache.put(sessionId, sessionId);
                    logger.debug("Session [{}] found in DB and cached", sessionId);
                    return true;}
                ).orElseGet( () -> {
                    logger.debug("Session [{}] not found in DB", sessionId);
                    return false;
                }
                );
    }

    @Override
    public Optional<String> getSessionUser(String sessionId) {
        if (cache.containsKey(sessionId)) return Optional.of(cache.get(sessionId));

        byte[] session = convert.uuidToBytes(UUID.fromString(sessionId));

        return userSessionRepository.findById(session)
                .map(entity -> {
                    byte[] user = entity.getUserId();
                    cache.put(sessionId, convert.bytesToUuid(user).toString());
                    return Arrays.toString(user);
                });
    }

    @Override
    public void createSession(String sessionId, UserSession user) {

    }

    @Override
    public void removeSession(String sessionId) {

    }
}
