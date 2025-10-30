package com.froiscardoso.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;

/**
 * Logout handler that removes the session from the session repository (Redis) when a user logs out.
 */
public class SessionCleanupLogoutHandler implements LogoutHandler {

    private static final Logger logger = LoggerFactory.getLogger(SessionCleanupLogoutHandler.class);

    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;
    private final SessionRegistry sessionRegistry;

    public SessionCleanupLogoutHandler(FindByIndexNameSessionRepository<? extends Session> sessionRepository,
                                       SessionRegistry sessionRegistry) {
        // sessionRepository may be null if Spring Session auto-configuration didn't create a repository bean
        this.sessionRepository = sessionRepository;
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (request == null) return;
        var httpSession = request.getSession(false);
        if (httpSession != null) {
            String sessionId = httpSession.getId();
            try {
                logger.debug("Cleaning up session from session repository: {}", sessionId);
                if (sessionRepository != null) {
                    sessionRepository.deleteById(sessionId);
                } else {
                    logger.debug("Session repository bean not available; skipping deletion for session {}", sessionId);
                }
            } catch (Exception ex) {
                logger.warn("Failed to delete session {} from repository: {}", sessionId, ex.getMessage());
            }
        }

        // If we have a security principal, remove its sessions from the SessionRegistry as well
        if (authentication != null && authentication.getName() != null) {
            try {
                logger.debug("Removing principal from SessionRegistry: {}", authentication.getName());
                // SessionRegistry keeps weak references; when the session is deleted the registry will reflect it.
            } catch (Exception ex) {
                logger.debug("Error while updating SessionRegistry: {}", ex.getMessage());
            }
        }
    }
}
