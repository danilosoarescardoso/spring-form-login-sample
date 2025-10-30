package com.froiscardoso.demo.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SecurityConfigContextTest {

    @Autowired
    ApplicationContext ctx;

    @Test
    void contextLoadsAndProvidesSecurityFilterChain() {
        assertThat(ctx.getBeanNamesForType(SecurityFilterChain.class)).isNotEmpty();
    }
}
