package com.froiscardoso.demo.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class AuthLoggingFailureHandler implements AuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuthLoggingFailureHandler.class);
    private final String failureUrl;

    public AuthLoggingFailureHandler(String failureUrl) {
        this.failureUrl = failureUrl;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String sessionId = request.getSession(false) != null ? request.getSession(false).getId() : "<no-session>";
        logger.warn("Authentication failure for request sessionId='{}': {}", sessionId, exception.getMessage());
        response.sendRedirect(failureUrl);
    }
}
