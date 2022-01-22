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
package com.casbin.shiro.example.realm;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.casbin.casdoor.entity.CasdoorUser;
import org.casbin.casdoor.service.CasdoorAuthService;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

/**
 * @author Yixiang Zhao (@seriouszyx)
 * @date 2022-01-01 21:43
 **/
public class AccountRealm extends AuthorizingRealm {

    @Resource
    private CasdoorAuthService casdoorAuthService;

    public AccountRealm() {
        setAuthenticationTokenClass(BearerToken.class);
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        BearerToken token = (BearerToken) authenticationToken;
        CasdoorUser user = null;
        try {
            user = casdoorAuthService.parseJwtToken(token.getToken());
        } catch (ParseException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        SecurityUtils.getSubject().getSession().setAttribute("name", user.getName());
        return new SimpleAuthenticationInfo(user, token.getCredentials(), getName());
    }
}
