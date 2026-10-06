package com.casbin.shiro.example.controller;

import com.casbin.shiro.example.dao.FooModel;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.BearerToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.casbin.casdoor.entity.User;
import org.casbin.casdoor.exception.AuthException;
import org.casbin.casdoor.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
public class IndexController {

    private static final String STATE = "casdoorState";
    private static final String ACCESS_TOKEN = "casdoorAccessToken";

    private final AuthService authService;

    @Value("${casdoor.redirect-url}")
    private String redirectUrl;

    public IndexController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping({"/", "/index"})
    public String index(Model model) {
        model.addAttribute("user", SecurityUtils.getSubject().getPrincipal());
        return "index";
    }

    @GetMapping("/foos")
    public String getFoos(Model model) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        model.addAttribute("user", user);
        model.addAttribute("foos", List.of(
                new FooModel(1L, "a"),
                new FooModel(2L, "b"),
                new FooModel(3L, "c")));
        return "foos";
    }

    @GetMapping("/login")
    public String login() {
        // a random state kept in the session protects the sign-in against CSRF
        String state = UUID.randomUUID().toString();
        SecurityUtils.getSubject().getSession().setAttribute(STATE, state);
        return "redirect:" + authService.getSigninUrl(redirectUrl, state);
    }

    @GetMapping("/login/oauth2")
    public String doLogin(String code, String state, RedirectAttributes attributes) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Object expectedState = session.removeAttribute(STATE);
        if (expectedState == null || !expectedState.equals(state)) {
            attributes.addFlashAttribute("error", "Invalid state, please sign in again.");
            return "redirect:/";
        }

        try {
            String token = authService.getOAuthToken(code, state);
            subject.login(new BearerToken(token));
            subject.getSession().setAttribute(ACCESS_TOKEN, token);
        } catch (AuthException | AuthenticationException e) {
            attributes.addFlashAttribute("error", "Sign-in failed: " + e.getMessage());
            return "redirect:/";
        }
        return "redirect:/foos";
    }

    @PostMapping("/logout")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        Object token = subject.getSession().getAttribute(ACCESS_TOKEN);
        if (token != null) {
            try {
                // also end the user's session in Casdoor, so that signing in again asks for the password
                authService.logoutCurrentSession(token.toString());
            } catch (RuntimeException e) {
                // the Casdoor session may already have ended
            }
        }
        subject.logout();
        return "redirect:/";
    }
}
