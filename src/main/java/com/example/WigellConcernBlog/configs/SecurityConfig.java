package com.example.WigellConcernBlog.configs;

import com.example.WigellConcernBlog.converters.JwtAuthConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthConverter jwtAuthConverter;

    @Autowired
    public SecurityConfig (JwtAuthConverter jwtAuthConverter) {

        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"FORBIDDEN\"}");
                        }))
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(HttpMethod.GET,  "/api/v2/posts", "api/v2/post/**").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/v2/count").hasRole("myclient_ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/v2/newpost").hasRole("myclient_USER")
                                .requestMatchers(HttpMethod.PUT, "/api/v2/updatepost/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/v2/deletepost/**").hasAnyRole("myclient_USER", "myclient_ADMIN")
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2
                                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter))
    );
    return http.build();
}
}