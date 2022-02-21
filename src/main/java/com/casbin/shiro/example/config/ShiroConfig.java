// Copyright 2021 The casbin Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.casbin.shiro.example.config;

import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.casbin.casdoor.config.CasdoorConfig;
import org.casbin.casdoor.shiro.CasdoorShiroRealm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author Yixiang Zhao (@seriouszyx)
 * @date 2021-12-29 21:02
 **/
@Configuration
public class ShiroConfig {

    @Resource
    private CasdoorConfig casdoorConfig;

    @Bean
    CasdoorShiroRealm simpleAccountRealm() {
        return new CasdoorShiroRealm(
                casdoorConfig.getEndpoint(),
                casdoorConfig.getClientId(),
                casdoorConfig.getClientSecret(),
                casdoorConfig.getJwtPublicKey(),
                casdoorConfig.getOrganizationName(),
                casdoorConfig.getApplicationName()
        );
    }

    @Bean
    public DefaultWebSecurityManager securityManager(CasdoorShiroRealm accountRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(accountRealm);
        return securityManager;
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinition("/login", "anon");
        chainDefinition.addPathDefinition("/login/oauth2", "anon");
        chainDefinition.addPathDefinition("/index", "anon");
        // all other paths require a logged in user
        chainDefinition.addPathDefinition("/**", "authc");
        return chainDefinition;
    }
}
