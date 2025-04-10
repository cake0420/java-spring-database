package com.cake7.database.scheduler;

import com.cake7.database.model.repository.UserSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.rmi.ServerException;

@Component
public class SessionCleanupScheduler {
    private final Logger logger = LoggerFactory.getLogger(SessionCleanupScheduler.class.getName());
    private final UserSessionRepository userSessionRepository;

    public SessionCleanupScheduler(UserSessionRepository userSessionRepository) {
        this.userSessionRepository = userSessionRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanExpiredSession() throws ServerException {
        try {
            int delete = userSessionRepository.deleteExpiredSessions();
            logger.debug("delete expired sessions: {}", delete);
        } catch (Exception e) {
            throw new ServerException("Error cleaning up expired sessions" + e.getMessage());
        }
    }
}
