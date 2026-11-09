package com.authentication.application.security.filter;

import com.authentication.application.helper.UserHelper;
import com.authentication.application.repositories.UserRepository;
import com.authentication.application.security.JwtService;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
 import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private  final Logger logger = Logger.getLogger(JwtAuthenticationFilter.class.getName());
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.info(" ----- filter executed ------ ");
        String header = request.getHeader("Authorization");

        if(header != null && header.startsWith("Bearer ")){

            String token = header.substring(7);
            try {
                if (jwtService.isAccessToken(token)) {
                    Jws<Claims> jws = jwtService.parseAccessToken(token);

                    Claims payload = jws.getPayload();
                    String username = payload.getSubject();
                    UUID userid = UserHelper.parseUUID(username);

                    userRepository.findById(userid).ifPresent(user -> {
                        if (user.isEnable()) {

                            List<GrantedAuthority> authorities = user.getRoles() == null ? Collections.emptyList() : user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());

                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                            }
                        }
                    });
                }
                }catch(ExpiredJwtException e){
                    e.printStackTrace();
                }catch(MalformedJwtException e){
                    e.printStackTrace();
                }catch(JwtException e){
                    e.printStackTrace();
                }

            catch(Throwable t){
                    t.printStackTrace();
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=utf-8");
                    Map<String, String> map = new HashMap<>();
                    map.put("error", "Invalid Token");
                    map.put("Status", "401");
                    response.getWriter().write(objectMapper.writeValueAsString(map));
                    return;
                }


        }

            filterChain.doFilter(request, response);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().startsWith("/api/v1/user/login") || request.getRequestURI().startsWith("/api/v1/auth/logout") || request.getRequestURI().startsWith("/api/v1/auth/register") || request.getRequestURI().startsWith("/o/oauth2/v2/auth");
    }
}
