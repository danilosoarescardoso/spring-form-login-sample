package com.froiscardoso.demo.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

/**
 * AuthenticationSuccessHandler that logs authentication and session details then delegates to a SimpleUrlAuthenticationSuccessHandler.
 */
public class AuthLoggingSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuthLoggingSuccessHandler.class);

    public AuthLoggingSuccessHandler(String defaultTargetUrl) {
        super(defaultTargetUrl);
        setAlwaysUseDefaultTargetUrl(true);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String sessionId = request.getSession(false) != null ? request.getSession(false).getId() : "<no-session>";
        logger.info("Authentication success for user='{}', sessionId='{}', authorities={}", authentication.getName(), sessionId, authentication.getAuthorities());
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
