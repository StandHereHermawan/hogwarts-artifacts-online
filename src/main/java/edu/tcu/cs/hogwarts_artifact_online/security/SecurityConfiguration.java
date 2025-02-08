package edu.tcu.cs.hogwarts_artifact_online.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class SecurityConfiguration {

    private final RSAPublicKey rsaPublicKey;

    private final RSAPrivateKey rsaPrivateKey;

    private final CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint;

    private final CustomBearerTokenAuthenticationEntryPoint customBearerTokenAuthenticationEntryPoint;

    private final CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler;

    @Value("${api.endpoint.base-url}")
    private String baseUrl;

    public SecurityConfiguration(CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint,
                                 CustomBearerTokenAuthenticationEntryPoint customBearerTokenAuthenticationEntryPoint,
                                 CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler)
            throws NoSuchAlgorithmException {
        this.customBasicAuthenticationEntryPoint = customBasicAuthenticationEntryPoint;
        this.customBearerTokenAuthenticationEntryPoint = customBearerTokenAuthenticationEntryPoint;
        this.customBearerTokenAccessDeniedHandler = customBearerTokenAccessDeniedHandler;

        // Generate a public/private keypair.
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // The generated key will have a size of 2048 bits.
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        this.rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        this.rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(authorizeHttpRequest
                                -> authorizeHttpRequest
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
                                .anyRequest().authenticated() /// Always a good idea to put this as last.
                        /// To make every url that not declarated in this class are protected by middleware.
                )
                // .headers(headers -> headers
                //         .frameOptions().disable())
                /// method ".headers(headers -> headers
                ///                 .frameOptions().disable())"
                /// above is Deprecated.
                ///
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin()))
                /// method ".headers(headers -> headers
                ///     .frameOptions(frameOptions -> frameOptions.sameOrigin()))" are
                /// expected solution for the deprecated "headers.frameOptions().disable()" method.
                ///
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpSecurityHttpBasicConfigurer
                        -> httpSecurityHttpBasicConfigurer
                        .authenticationEntryPoint(this.customBasicAuthenticationEntryPoint))
                ///
                // .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer
                //                 -> httpSecurityOAuth2ResourceServerConfigurer.jwt()
                //                 .and().authenticationEntryPoint(this.customBearerTokenAuthenticationEntryPoint)
                //                 .accessDeniedHandler(this.customBearerTokenAccessDeniedHandler))
                /// method ".oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer
                ///                         -> httpSecurityOAuth2ResourceServerConfigurer.jwt()
                ///                         .and().authenticationEntryPoint(this.customBearerTokenAuthenticationEntryPoint)
                ///                         .accessDeniedHandler(this.customBearerTokenAccessDeniedHandler))"
                /// above is deprecated.
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer
                        -> httpSecurityOAuth2ResourceServerConfigurer
                        .accessDeniedHandler(this.customBearerTokenAccessDeniedHandler)
                        .authenticationEntryPoint(this.customBearerTokenAuthenticationEntryPoint)
                        .jwt(Customizer.withDefaults()))
                /// method ".oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer
                ///                                 -> httpSecurityOAuth2ResourceServerConfigurer
                ///                                 .jwt(Customizer.withDefaults()))" above
                /// are expected solution for the deprecated
                /// ".oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer
                ///                  -> httpSecurityOAuth2ResourceServerConfigurer.jwt())" method.
                .sessionManagement(
                        httpSecuritySessionManagementConfigurer
                                -> httpSecuritySessionManagementConfigurer
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        /// JWK stands for JSON Web Key.
        JWK jwk = new RSAKey.Builder(this.rsaPublicKey)
                .privateKey(this.rsaPrivateKey)
                .build();

        JWKSource<SecurityContext> jwkSet = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.rsaPublicKey).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;
        {
            jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
            ///
            jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
            jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        }
        JwtAuthenticationConverter jwtAuthenticationConverter;
        {
            jwtAuthenticationConverter = new JwtAuthenticationConverter();
            jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        }
        return jwtAuthenticationConverter;
    }
}
