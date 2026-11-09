package com.authentication.application.config;

import com.authentication.application.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    private final AuthenticationSuccessHandler authenticationSuccessHandler;


    //    @Bean
//    public UserDetailsService userDetailsService(){
//
//        User.UserBuilder builder = User.withDefaultPasswordEncoder();
//
//        UserDetails ud = builder.username("aakarsh").password("admin").roles("ADMIN").build();
//        UserDetails ud1 = builder.username("admin2").password("admin").roles("ADMIN").build();
//
//        return new InMemoryUserDetailsManager(ud,ud1);
//    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info(" ----- filter executed ------ ");
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathPatternRequestMatcher.withDefaults()
                                .matcher(HttpMethod.POST, "/api/v1/auth/register")).permitAll()
                        .requestMatchers( AppConstants.AUTH_PUBLIC_URLS ).permitAll()
                        .requestMatchers(PathPatternRequestMatcher.withDefaults().matcher("/h2-console/**")).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 ->
                            oauth2.successHandler(authenticationSuccessHandler)
                                    .failureHandler(null)
                        )
                .logout(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )
                .exceptionHandling(ex -> {
                    ex.authenticationEntryPoint((req, resp, e) -> {

                        e.printStackTrace();
                        resp.setStatus(401);
                        resp.setContentType("application/json");
                        String msg = "unauthorized access "+e.getMessage();
                        Map<String, String> map = new HashMap<>();
                        map.put("message", msg);
                        map.put("status", "401");
                        resp.getWriter().write(objectMapper.writeValueAsString(map));
                        resp.setCharacterEncoding("UTF-8");

                    });
                })
                .addFilterBefore(jwtAuthenticationFilter    , UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // React App Origin
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
