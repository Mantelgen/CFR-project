package com.cfr.networkapp.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfig {

    	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    		http.csrf(AbstractHttpConfigurer::disable)
        	    .authorizeHttpRequests(auth -> auth
            	    .requestMatchers("/api/status").permitAll()
            	    .requestMatchers("/api/info").permitAll()
            	    .requestMatchers("/api/mail/**").permitAll()
                    .requestMatchers("/login").permitAll()
            	    .anyRequest().authenticated()
        	    )
        	    .formLogin(form -> form
    			.defaultSuccessUrl("/api/trains", true)
   	 		.permitAll()
			)
        	    .logout(logout -> logout.permitAll());

    		return http.build();
	}

	@Bean
	public UserDetailsService users() {
    		UserDetails user = User.withUsername("admin")
            	.password("{noop}password")
            	.roles("USER")
            	.build();

    		return new InMemoryUserDetailsManager(user);
	}
}
