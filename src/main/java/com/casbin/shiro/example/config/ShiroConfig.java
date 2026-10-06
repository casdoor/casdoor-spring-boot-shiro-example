package com.casbin.shiro.example.config;

import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.casbin.casdoor.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroConfig {

    // AuthService comes from casdoor-spring-boot-starter, configured by the casdoor.* properties
    @Bean
    public CasdoorShiroRealm casdoorShiroRealm(AuthService authService) {
        return new CasdoorShiroRealm(authService);
    }

    // the realm is an Authorizer too, so Shiro's auto-configuration skips this bean but still looks it up by name
    @Bean
    public Authorizer authorizer() {
        return new ModularRealmAuthorizer();
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinition("/", "anon");
        chainDefinition.addPathDefinition("/index", "anon");
        chainDefinition.addPathDefinition("/login", "anon");
        chainDefinition.addPathDefinition("/login/oauth2", "anon");
        chainDefinition.addPathDefinition("/error", "anon");
        // all other paths require a signed-in user
        chainDefinition.addPathDefinition("/**", "authc");
        return chainDefinition;
    }
}
