package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    // Configurable CORS origins for different environments
    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:4200,http://127.0.0.1:3000}")
    private String[] allowedOrigins;

    public SecurityConfig(UserDetailsService userDetailsService, JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        System.out.println("Creating SecurityFilterChain bean...");
        return http
                .csrf(csrf -> csrf.disable()) // Documented: API uses JWT tokens, CSRF not needed for stateless REST API
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> {
                    System.out.println("Configuring authorization rules...");
                    auth
                            // Public endpoints
                            .requestMatchers("/api/auth/**").permitAll()
                            .requestMatchers("/api/test/public").permitAll()
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            // Admin only endpoints
                            .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.POST, "/api/quizzes").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/api/quizzes/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/quizzes/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.POST, "/api/questions").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/api/questions/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/questions/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.POST, "/api/answers").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/api/answers/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/answers/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.POST, "/api/stories").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/api/stories/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/stories/**").hasRole("ADMIN")
                            // All other endpoints require authentication
                            .anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    System.out.println("Authentication exception: " + authException.getMessage());
                    response.setStatus(401);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Unauthorized access\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    System.out.println("Access denied: " + accessDeniedException.getMessage());
                    response.setStatus(403);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Access denied\"}");
                })
                )
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Restrict CORS to trusted origins - configurable per environment
        if (allowedOrigins != null && allowedOrigins.length > 0) {
            configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));
        } else {
            // Fallback for development
            configuration.setAllowedOriginPatterns(List.of(
                    "http://localhost:3000",
                    "http://localhost:4200",
                    "http://localhost:8080",
                    "http://127.0.0.1:*"
            ));
        }

        // Restrict HTTP methods to only what's needed
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Restrict headers to necessary ones
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With"
        ));

        // Security: Don't allow credentials for enhanced security
        configuration.setAllowCredentials(false);

        // Cache preflight requests for performance
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
