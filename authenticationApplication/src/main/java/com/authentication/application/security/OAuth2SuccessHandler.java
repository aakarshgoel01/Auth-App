package com.authentication.application.security;

import com.authentication.application.entity.Provider;
import com.authentication.application.entity.RefreshToken;
import com.authentication.application.entity.User;
import com.authentication.application.repositories.RefreshTokenRepository;
import com.authentication.application.repositories.UserRepository;
import com.authentication.application.service.CookieService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieService cookieService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("onAuthenticationSuccess ----> "+authentication.toString());
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String registrationId = "unknown";

        if(authentication instanceof OAuth2AuthenticationToken authenticationToken){
            registrationId = authenticationToken.getAuthorizedClientRegistrationId();
        }
        String name = "", email = "";
        log.info("onAuthenticationSuccess ----> "+registrationId);
        User user;
        switch(registrationId){
            case "google":
                String googleid = oauthUser.getAttributes().getOrDefault("sub","").toString();
                 name = oauthUser.getAttributes().getOrDefault("name","").toString();
                 email = oauthUser.getAttributes().getOrDefault("email","").toString();
                String picture = oauthUser.getAttributes().getOrDefault("picture","").toString();
                log.info("onAuthenticationSuccess ----> "+name+" "+email);

                user = User.builder()
                        .name(name)
                        .email(email)
                        .image(picture)
                        .password(UUID.randomUUID().toString())
                        .provider(Provider.GOOGLE)
                        .build();

                userRepository.findByEmail(email).ifPresentOrElse(user1 -> {
                    log.info("User is there is db. ");
                    log.info("onAuthenticationSuccess ----> "+user1.toString());
                }, () -> {
                    userRepository.save(user);
                });
                break;
            case "github":
                name = oauthUser.getAttributes().getOrDefault("login","").toString();
                String githubid = oauthUser.getAttributes().getOrDefault("id","").toString();
                String image = oauthUser.getAttributes().getOrDefault("avatar_url","").toString();

                email = (String) oauthUser.getAttributes().get("email");
                if (email == null) {
                    email = name + "@github.com";
                }

                User newUser = User.builder()
                        .email(email)
                        .name(name)
                        .image(image)
                        .password(UUID.randomUUID().toString())
                        .enable(true)
                        .provider(Provider.GITHUB)
                        .build();
                user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(newUser));
                break;

            default:
                    throw new RuntimeException("Invalid registration id");

        }

        UUID jti = UUID.randomUUID();
        RefreshToken refreshTokenDB = RefreshToken.builder()
                .jti(jti.toString())
                .revoked(false)
                .user(user)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds())).build();
        refreshTokenRepository.save(refreshTokenDB);

        String refreshToken = jwtService.generateRefreshToken(user, jti.toString());

        String accessToken = jwtService.generateAccessToken(user);

        cookieService.attachRefreshCookie(response, refreshToken, (int)jwtService.getRefreshTtlSeconds());
        response.getWriter().write("Hiiiii ----- logged in successfully");

    }
}
