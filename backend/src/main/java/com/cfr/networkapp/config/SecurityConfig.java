package com.cfr.networkapp.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Enable CSRF except for stateless endpoints
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/auth/**", "/api/info", "/api/status", "/api/mail/**")
            )
            // Enable CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Secure headers
            .headers(headers -> headers
                .xssProtection(xss -> xss.block(true))
                .frameOptions(frame -> frame.sameOrigin())
                .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
            )
            // Session management
            .sessionManagement(session -> session
                .sessionFixation().migrateSession()
            )
            // Authorization
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/status").permitAll()
                .requestMatchers("/api/info").permitAll()
                .requestMatchers("/api/mail/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/trains/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/trains", "/api/trains/**").permitAll()
                // Only authenticated users can POST/PUT/DELETE trains
                .requestMatchers(HttpMethod.POST, "/api/trains").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/trains/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/trains/**").authenticated()
                // Only authenticated users can access reservations
                .requestMatchers("/api/reservations/**").authenticated()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/register").permitAll()
                .requestMatchers("/").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/api/search", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .permitAll()
            );

        return http.build();
    }

    // CORS configuration allowing only your domain (adjust as needed)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://cfr.local", "http://cfr.local"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

