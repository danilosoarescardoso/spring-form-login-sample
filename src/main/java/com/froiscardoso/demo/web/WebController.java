package com.froiscardoso.demo.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class WebController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

@GetMapping("/")
public String home(Principal principal, Model model) {
    if (principal == null) {
        logger.warn("Tentativa de acesso à página inicial sem autenticação");
        return "redirect:/login";
    }
    logger.info("Usuário '{}' acessando a página inicial", principal.getName());
    model.addAttribute("username", principal.getName());
    return "home";
}

@GetMapping("/login")
public String login(
    @RequestParam(value = "error", required = false) String error,
    @RequestParam(value = "logout", required = false) String logout,
    @RequestParam(value = "expired", required = false) String expired,
    Model model) {
    
    logger.debug("Acessando página de login - error: {}, logout: {}, expired: {}", error, logout, expired);
    
    if (error != null) {
        logger.warn("Erro de autenticação detectado");
        model.addAttribute("errorMsg", "Usuário ou senha inválidos.");
    }
    if (logout != null) {
        logger.info("Logout realizado com sucesso");
        model.addAttribute("msg", "Você saiu com sucesso.");
    }
    if (expired != null) {
        logger.info("Sessão expirada detectada");
        model.addAttribute("errorMsg", "Sua sessão expirou. Por favor, faça login novamente.");
    }
    return "login";
}
}