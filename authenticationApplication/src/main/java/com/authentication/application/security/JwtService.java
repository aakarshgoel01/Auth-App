package com.authentication.application.security;

import com.authentication.application.entity.Role;
import com.authentication.application.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
@Getter
@Setter
public class JwtService {

    private final SecretKey secretKey;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;
    private final String issuer;

    public JwtService(@Value("${security.jwt.secret}") String secretKey, @Value("${security.jwt.issuer}") String issuer, @Value("${security.jwt.access-ttl-seconds}") long accessTtlSeconds, @Value("${security.jwt.refresh-ttl-seconds}") long refreshTtlSeconds ) {

        if(secretKey == null || secretKey.length() < 64) {
            throw new IllegalArgumentException("Secret key must be at least 64 characters long");
        }
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTtlSeconds = accessTtlSeconds;
        this.refreshTtlSeconds = refreshTtlSeconds;
        this.issuer = issuer;

    }

    // generate Token
    public String generateAccessToken(User user) {

        Instant now = Instant.now();
        List<String> roles = new ArrayList<>();
        if(user.getRoles() != null && !user.getRoles().isEmpty()) {
            roles = user.getRoles().stream().map(Role::getName).toList();
        }
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .claims(Map.of(
                        "email" , user.getEmail(),
                        "roles" , roles,
                        "typ" , "access"
                ))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

    }

    //refresh Token
    public String generateRefreshToken(User user, String jti) {

        Instant now = Instant.now();
        List<String> roles = new ArrayList<>();
        if(user.getRoles() != null && !user.getRoles().isEmpty()) {
           roles = user.getRoles().stream().map(Role::getName).toList();
        }

        return Jwts.builder()
                .id(jti)
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .claims(Map.of(
                        "email" , user.getEmail(),
                        "roles" , roles,
                        "typ" , "refresh"
                ))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

    }

    public Jws<Claims> parseAccessToken(String jti) {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseClaimsJws(jti);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAccessToken(String jti) {
        Claims c = parseAccessToken(jti).getPayload();
        return "access".equals(c.get("typ"));
    }

    public boolean isRefreshToken(String jti) {
        if(jti != null) {
            Claims c = parseAccessToken(jti).getPayload();
            return "refresh".equals(c.get("typ"));
        }else {
            return false;
        }
    }

    public UUID getUserId(String jti) {
        Claims c = parseAccessToken(jti).getPayload();
        return UUID.fromString((String) c.getSubject());
    }

    public String getJti(String jti) {
        return parseAccessToken(jti).getPayload().getId();
    }

}
