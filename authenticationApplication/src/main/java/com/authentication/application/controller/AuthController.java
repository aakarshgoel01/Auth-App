package com.authentication.application.controller;


import com.authentication.application.dto.LoginRequest;
import com.authentication.application.dto.TokenResponse;
import com.authentication.application.dto.UserDto;
import com.authentication.application.entity.RefreshToken;
import com.authentication.application.entity.User;
import com.authentication.application.repositories.RefreshTokenRepository;
import com.authentication.application.repositories.UserRepository;
import com.authentication.application.security.JwtService;
import com.authentication.application.service.AuthService;
import com.authentication.application.service.CookieService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtService tokenService;

    private final ModelMapper modelMapper;

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtService jwtService;

    private final CookieService cookieService;


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> loginUser(@RequestBody LoginRequest authReq, HttpServletResponse response ) {

       Authentication   auth = authenticate(authReq);

       User user = userRepository.findByEmail(auth.getName()).get();

       if(user == null || !user.isEnable()){

           throw new BadCredentialsException("Invalid username or password/ User is disabled !!!");

       }

       String jti = UUID.randomUUID().toString();

        RefreshToken refreshTokenObj = RefreshToken.builder()
                                                    .jti(jti)
                                                    .user(user)
                                                    .issuedAt(Instant.now())
                                                    .expiresAt(Instant.now().plusSeconds(tokenService.getRefreshTtlSeconds()))
                                                    .revoked(false)
                                                    .build();

        refreshTokenRepository.save(refreshTokenObj);

       String uuidToken = tokenService.generateAccessToken(user);
       String refreshToken = tokenService.generateRefreshToken(user, refreshTokenObj.getJti());

       cookieService.attachRefreshCookie(response, refreshToken, (int) tokenService.getRefreshTtlSeconds());

        TokenResponse tokenResponse = TokenResponse.of(uuidToken, refreshToken, tokenService.getAccessTtlSeconds(),modelMapper.map(user, UserDto.class) );

       return ResponseEntity.ok(tokenResponse);

    }

    private Authentication authenticate(LoginRequest authReq) {

        try {
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authReq.email(), authReq.password()));
        } catch (Throwable e) {
            throw new BadCredentialsException("Invalid username or password");
        }

    }


    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(userDto));

    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            HttpServletResponse response,
            HttpServletRequest request
    ) {

        String refreshToken = readRefreshTokenFromRequest(request).orElseThrow(()-> new BadCredentialsException("Refresh Token does not Exisit. "));

        if(!jwtService.isRefreshToken(refreshToken)){
            throw new BadCredentialsException("Invalid Refresh Token");
        }

        String jti = jwtService.getJti(refreshToken);
        UUID userId = jwtService.getUserId(refreshToken);

        RefreshToken rToken = refreshTokenRepository.findByJti(jti);

        if(rToken == null){
            throw new BadCredentialsException("Invalid Refresh Token");
        }

        if(!rToken.getUser().getId().equals(userId)){
            throw new BadCredentialsException("Invalid Refresh Token");
        }

        if(rToken.getExpiresAt().isBefore(Instant.now())){
            throw new BadCredentialsException("Refresh token already expired");
        }

        rToken.setRevoked(true);
        String newJti = UUID.randomUUID().toString();

        rToken.setReplacedByToken(newJti);
        refreshTokenRepository.save(rToken);

        User user = rToken.getUser();

        RefreshToken newRefreshTokenDb = RefreshToken.builder().jti(newJti)
                .user(user)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(tokenService.getRefreshTtlSeconds()))
                .revoked(false)
                .build();
        refreshTokenRepository.save(newRefreshTokenDb);

        var newRefreshToken = jwtService.generateRefreshToken(user, newJti);

        var newAccessToken = jwtService.generateAccessToken(user);

        cookieService.attachRefreshCookie(response, refreshToken, (int) tokenService.getRefreshTtlSeconds());
        cookieService.clearRefreshCookie(response);

        return ResponseEntity.ok(TokenResponse.of(newAccessToken, newRefreshToken, jwtService.getRefreshTtlSeconds(), modelMapper.map(user, UserDto.class) ));


    }

    private Optional<String> readRefreshTokenFromRequest(HttpServletRequest request) {

        if(request.getCookies() != null) {
            Optional<String> token = Arrays.stream(request.getCookies()).filter(cookie -> cookieService.getRefreshTokenCookieName().equals(cookie.getName()))
                    .findFirst()
                    .map(cookie -> cookie.getValue())
                    .filter(c -> !c.isBlank())
                    .stream().findFirst();
            if(token.isPresent()) {
                return token;
            }
        }
        return null;
    }

    @PostMapping("/logout")
    private ResponseEntity<Void> logout(HttpServletResponse response, HttpServletRequest request) {

        String refreshToken = readRefreshTokenFromRequest(request) != null?readRefreshTokenFromRequest(request).get():null ;
            try {

                if(refreshToken!= null && jwtService.isRefreshToken(refreshToken)){
                    String jti = jwtService.getJti(refreshToken);
                    RefreshToken rt = refreshTokenRepository.findByJti(jti);
                    rt.setRevoked(true);
                    refreshTokenRepository.save(rt);
                }

            } catch (Exception e) {
                System.err.println("Excetion -------->  " + e.getMessage());
            }


        cookieService.addNoStoreHeaders(response);
        cookieService.clearRefreshCookie(response);
        SecurityContextHolder.clearContext();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
