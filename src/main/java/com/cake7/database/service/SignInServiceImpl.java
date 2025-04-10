package com.cake7.database.service;

import com.cake7.database.domain.UserSession;
import com.cake7.database.domain.Users;
import com.cake7.database.model.dto.SignInRequestDTO;
import com.cake7.database.model.repository.UserRepository;
import com.cake7.database.model.repository.UserSessionRepository;
import com.cake7.database.util.Encrypt;
import com.cake7.database.util.UuidToBinary;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.rmi.ServerException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
public class SignInServiceImpl implements SignInService {
    private final HttpServletRequest request;
    Logger logger = LoggerFactory.getLogger(SignInServiceImpl.class);
    private final UserRepository userRepository;
    private final Encrypt encrypt;
    private final UuidToBinary uuidToBinary;
    private final UserSessionRepository userSessionRepository;

    public SignInServiceImpl(HttpServletRequest request, UserRepository userRepository, Encrypt encrypt, UuidToBinary uuidToBinary, UserSessionRepository userSessionRepository) {
        this.request = request;
        this.userRepository = userRepository;
        this.encrypt = encrypt;
        this.uuidToBinary = uuidToBinary;
        this.userSessionRepository = userSessionRepository;
    }

    public byte[] signIn(SignInRequestDTO signInRequestDTO) throws ServerException {
        try {
            Optional<Users> user = userRepository.findByEmail(signInRequestDTO.email());
            LocalDateTime now = LocalDateTime.now();
            UUID sessionUUID = UUID.randomUUID();

            if (user.isPresent()) {
                String encryptPassword = encrypt.getEncrypt(signInRequestDTO.password(), user.get().getSalt());
                if(user.get().getPassword().equals(encryptPassword)) {
                    logger.debug(user.get().getEmail());

                    UserSession userSession = new UserSession(
                                                                uuidToBinary.uuidToBytes(sessionUUID),
                                                                user.get().getId(),
                                                                request.getRemoteAddr(),
                                                                request.getHeader("User-Agent"),
                                                                now,
                                                                now,
                                                                now.plusDays(7),
                                                                true
                                                            );
                    userSessionRepository.save(userSession);
                    logger.debug(userSession.toString());
                    return userSession.getSessionId();
                }
            }
            return null;
        }
        catch (Exception e) {
            logger.error("Error during sign in: " + e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }
}
