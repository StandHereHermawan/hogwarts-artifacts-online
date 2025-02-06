package edu.tcu.cs.hogwarts_artifact_online.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfiguration {

    @Value("${api.endpoint.base-url}")
    private String baseUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(authorizeHttpRequest -> authorizeHttpRequest
                        /// Code description was the comment below the declarated code.
                        .requestMatchers(HttpMethod.GET, this.baseUrl + "/artifacts/**")
                        .permitAll()
                        /// permitAll() methods make declarated url
                        /// in requestMatchers() param no need for authentication.
                        ///
                        .requestMatchers(HttpMethod.GET, this.baseUrl + "/users/**")
                        .hasAuthority("ROLE_admin")
                        /// Protecting declaratedURL endpoint requestMatchers().
                        /// hasAuthority() methods make declarated url
                        /// in requestMatchers() param only can be accessed by user having ROLE_admin.
                        ///
                        .requestMatchers(HttpMethod.POST, this.baseUrl + "/users")
                        .hasAuthority("ROLE_admin")
                        /// Protecting declaratedURL endpoint requestMatchers().
                        /// hasAuthority() methods make declarated url
                        /// in requestMatchers() param only can be accessed by user having ROLE_admin.
                        ///
                        .requestMatchers(HttpMethod.PUT, this.baseUrl + "/users/**")
                        .hasAuthority("ROLE_admin")
                        /// Protecting declaratedURL endpoint requestMatchers().
                        /// hasAuthority() methods make declarated url
                        /// in requestMatchers() param only can be accessed by user having ROLE_admin.
                        ///
                        .requestMatchers(HttpMethod.DELETE, this.baseUrl + "/users/**")
                        .hasAuthority("ROLE_admin")
                        /// Protecting declaratedURL endpoint requestMatchers().
                        /// hasAuthority() methods make declarated url
                        /// in requestMatchers() param only can be accessed by user having ROLE_admin.
                        ///
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**"))
                        .permitAll()
                        /// To Make the ``/h2-console`` endpoint accessible without needing authentication.
                        ///
                        .anyRequest().authenticated()
                        /// To make every url that not declarated in this class are protected by middleware.
                )
                // .headers(headers -> headers
                //         .frameOptions().disable())
                /// method ".headers(headers -> headers
                ///                 .frameOptions().disable())"
                /// above are Deprecated.
                ///
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin()))
                /// method ".headers(headers -> headers
                ///     .frameOptions(frameOptions -> frameOptions.sameOrigin()))" are
                /// expected solution for the deprecated "headers.frameOptions().disable()" method.
                ///
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
