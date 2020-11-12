package com.quizmaster.backend.web;

import com.quizmaster.backend.filters.JWTAuthorizationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${com.quizmaster.backend.configs.google_client_id}")
    private String clientId;


    @Override
    protected void configure(HttpSecurity security) throws Exception
    {
        security.httpBasic().disable();
        getHttp().cors().and().csrf().disable();
        getHttp().antMatcher("/quizzes/**").authorizeRequests() //
                .anyRequest().authenticated() //
                .and()
                .addFilterBefore(jwtAuthFilter(), JWTAuthorizationFilter.class);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JWTAuthorizationFilter jwtAuthFilter() throws Exception{
        return new JWTAuthorizationFilter(authenticationManager(), clientId);
    }
}