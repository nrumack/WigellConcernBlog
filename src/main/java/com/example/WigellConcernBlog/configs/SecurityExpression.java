package com.example.WigellConcernBlog.configs;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("security")
public class SecurityExpression {
    public String getKeycloakId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaim("sub");
        }
        return null;
    }
}
