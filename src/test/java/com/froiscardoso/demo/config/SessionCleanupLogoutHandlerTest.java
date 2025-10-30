package com.froiscardoso.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SessionCleanupLogoutHandlerTest {

    private FindByIndexNameSessionRepository<Session> repository;
    private SessionRegistry sessionRegistry;
    private SessionCleanupLogoutHandler handler;

    @BeforeEach
    void setUp() {
        repository = mock(FindByIndexNameSessionRepository.class);
        sessionRegistry = mock(SessionRegistry.class);
        handler = new SessionCleanupLogoutHandler(repository, sessionRegistry);
    }

    @Test
    void logoutDeletesSessionWhenPresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.getSession(true).setAttribute("dummy", "value");
        String sessionId = request.getSession(false).getId();

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user");

        handler.logout(request, response, auth);

        verify(repository).deleteById(ArgumentMatchers.eq(sessionId));
    }

    @Test
    void logoutSkipsWhenRepositoryNull() {
        // create handler with null repository
        handler = new SessionCleanupLogoutHandler(null, sessionRegistry);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.getSession(true);
        Authentication auth = mock(Authentication.class);
        handler.logout(request, response, auth);
        // no exception should be thrown; repository is null so nothing to verify
    }
}
