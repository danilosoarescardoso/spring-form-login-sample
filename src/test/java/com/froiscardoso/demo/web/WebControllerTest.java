package com.froiscardoso.demo.web;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.security.Principal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class WebControllerTest {

    @Test
    void homeRedirectsToLoginWhenNoPrincipal() throws Exception {
    WebController controller = new WebController();
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/");
    viewResolver.setSuffix(".html");
    MockMvc mvc = MockMvcBuilders.standaloneSetup(controller)
        .setViewResolvers(viewResolver)
        .build();

        mvc.perform(get("/"))
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void homeShowsUsernameWhenPrincipalPresent() throws Exception {
    WebController controller = new WebController();
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/");
    viewResolver.setSuffix(".html");
    MockMvc mvc = MockMvcBuilders.standaloneSetup(controller)
        .setViewResolvers(viewResolver)
        .build();

        Principal principal = () -> "alice";

        mvc.perform(get("/").principal(principal))
                .andExpect(view().name("home"))
                .andExpect(model().attribute("username", "alice"));
    }

    @Test
    void loginPageShowsMessages() throws Exception {
    WebController controller = new WebController();
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/");
    viewResolver.setSuffix(".html");
    MockMvc mvc = MockMvcBuilders.standaloneSetup(controller)
        .setViewResolvers(viewResolver)
        .build();

        mvc.perform(get("/login").param("logout", "true"))
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("msg"));

        mvc.perform(get("/login").param("error", "true"))
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("errorMsg"));
    }
}
