package com.casbin.shiro.example.config;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.BearerToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.casbin.casdoor.entity.User;
import org.casbin.casdoor.exception.AuthException;
import org.casbin.casdoor.service.AuthService;

/**
 * A Shiro realm that signs in with a Casdoor access token: the token is a JWT, verified with the certificate
 * of the application. The principal is the Casdoor {@link User}, and its Casdoor roles become Shiro roles.
 */
public class CasdoorShiroRealm extends AuthorizingRealm {

    private final AuthService authService;

    public CasdoorShiroRealm(AuthService authService) {
        this.authService = authService;
        setAuthenticationTokenClass(BearerToken.class);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        BearerToken token = (BearerToken) authenticationToken;
        try {
            User user = authService.parseJwtToken(token.getToken());
            return new SimpleAuthenticationInfo(user, token.getCredentials(), getName());
        } catch (AuthException e) {
            throw new AuthenticationException("Could not validate the Casdoor access token", e);
        }
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        User user = (User) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        if (user.roles != null) {
            user.roles.forEach(role -> info.addRole(role.name));
        }
        return info;
    }
}
