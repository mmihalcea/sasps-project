package edu.saspsproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable()) // Disable CSRF for REST API
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - no authentication needed
                .requestMatchers("/api/auth/**").permitAll() // Login/Register endpoints
                .requestMatchers("/api/county/**").permitAll()
                .requestMatchers("/api/appointment/institutions/**").permitAll()
                .requestMatchers("/api/appointment/customer/**").permitAll()
                .requestMatchers("/api/appointment").permitAll()
                .requestMatchers("/api/user/**").permitAll() // Allow user creation/lookup
                
                // BASELINE: Admin endpoints are public for comparison without Keycloak
                .requestMatchers("/api/notifications/**").permitAll() // Temporarily public for baseline
                .requestMatchers("/api/admin/**").permitAll() // Temporarily public for baseline
                
                // All other requests are allowed (for baseline comparison)
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter()))
                .authenticationEntryPoint((request, response, authException) -> {
                    // Return 401 only if trying to access protected resources
                    response.sendError(401, "Unauthorized");
                })
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:8090"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
