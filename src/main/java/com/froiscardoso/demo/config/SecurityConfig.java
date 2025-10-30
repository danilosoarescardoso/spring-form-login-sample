package com.froiscardoso.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class SecurityConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

@Bean
public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
}

@Bean
public org.springframework.beans.factory.ObjectProvider<org.springframework.session.FindByIndexNameSessionRepository<? extends org.springframework.session.Session>> findByIndexNameSessionRepositoryProvider(org.springframework.beans.factory.ObjectProvider<org.springframework.session.FindByIndexNameSessionRepository<? extends org.springframework.session.Session>> provider) {
    return provider;
}

@Bean
public org.springframework.security.web.authentication.logout.LogoutHandler sessionCleanupLogoutHandler(
        org.springframework.beans.factory.ObjectProvider<org.springframework.session.FindByIndexNameSessionRepository<? extends org.springframework.session.Session>> sessionRepositoryProvider,
        SessionRegistry sessionRegistry) {
    var repo = sessionRepositoryProvider.getIfAvailable();
    return new SessionCleanupLogoutHandler(repo, sessionRegistry);
}

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http, SessionRegistry sessionRegistry, LogoutHandler logoutHandler) throws Exception {
    AuthLoggingSuccessHandler successHandler = new AuthLoggingSuccessHandler("/");
    AuthLoggingFailureHandler failureHandler = new AuthLoggingFailureHandler("/login?error=true");
    
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/h2-console/**",
                "/login",
                "/j_security_check",
                "/css/**",
                "/js/**",
                "/.well-known/**",
                "/error"
            ).permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/j_security_check")
            .successHandler(successHandler)
            .failureHandler(failureHandler)
            .usernameParameter("j_username")
            .passwordParameter("j_password")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/perform_logout")
            .addLogoutHandler(logoutHandler)
            .logoutSuccessUrl("/login?logout=true")
            .permitAll()
        )
        // liberar frames e CSRF para o console H2
        .headers(headers -> headers.frameOptions(frame -> frame.disable()))
        .csrf(csrf -> csrf.ignoringRequestMatchers(
            request -> request.getServletPath().startsWith("/h2-console"),
            request -> request.getServletPath().equals("/j_security_check")
        ))
        .sessionManagement(session -> session
            .maximumSessions(1)
            .maxSessionsPreventsLogin(true)
            .expiredUrl("/login?expired=true")
            .sessionRegistry(sessionRegistry))
        .sessionManagement(session -> session
            .sessionFixation().migrateSession());

    logger.info("Configuração de segurança inicializada com limite de 2 sessões por usuário");

    return http.build();
}

    @Bean
    public UserDetailsService users(PasswordEncoder passwordEncoder) {
        var user1 = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .build();

        var admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("adminpass"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user1, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}