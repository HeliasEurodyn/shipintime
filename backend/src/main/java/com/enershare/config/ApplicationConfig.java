package com.enershare.config;

import com.enershare.exception.ApplicationException;
import com.enershare.model.user.User;
import com.enershare.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.springframework.http.HttpHeaders.*;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    @Value("${cors.origin.url}")
    private String corsOriginUrl;



    @Bean
    public UserDetailsService userDetailsService() {
        return userId  -> userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException("1001","User Not Found By Id"));

    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {

                String name = authentication.getName();
                String cred = authentication.getCredentials().toString();

                User user = userRepository.findByUsernameOrPhone(name)
                        .orElseThrow(() -> new ApplicationException("1000","User Not Found By Phone"));

                if (user.getLogincode() != null && user.getLogincode().equals(cred)) {
                    return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                }

                if (user.getPassword() != null && passwordEncoder().matches(cred, user.getPassword())) {
                    return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                }

                throw new BadCredentialsException("Invalid Credentials");
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // config.setAllowedOriginPatterns(Arrays.asList("*"));
        // config.setAllowedOrigins(List.of(corsOriginUrl));
        // config.setAllowedOrigins("*");
        config.setAllowedOriginPatterns(Arrays.asList(corsOriginUrl));
        config.setAllowedHeaders(List.of(ORIGIN, CONTENT_TYPE, ACCEPT, AUTHORIZATION));
        config.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT", "PATCH"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}
