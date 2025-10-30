package com.froiscardoso.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.session.HttpSessionCreatedEvent;
import org.springframework.stereotype.Component;

@Component
public class SessionEventListener {

    private static final Logger logger = LoggerFactory.getLogger(SessionEventListener.class);
    private final SessionRegistry sessionRegistry;

    public SessionEventListener(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @EventListener
    public void onSessionCreated(HttpSessionCreatedEvent event) {
        logger.info("Nova sessão criada: {}", event.getSession().getId());
        logActiveSessionsCount();
    }

    @EventListener
    public void onSessionDestroyed(SessionDestroyedEvent event) {
        logger.info("Sessão destruída: {}", event.getId());
        event.getSecurityContexts().forEach(securityContext -> {
            String username = securityContext.getAuthentication().getName();
            logger.info("Usuário '{}' desconectado", username);
        });
        logActiveSessionsCount();
    }

    private void logActiveSessionsCount() {
        sessionRegistry.getAllPrincipals().forEach(principal -> {
            int activeSessionCount = sessionRegistry.getAllSessions(principal, false).size();
            logger.info("Usuário '{}' tem {} sessões ativas", principal, activeSessionCount);
        });
    }
}