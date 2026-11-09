package com.authentication.application.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
public class OAuth2RedirectController {

    @GetMapping("/login/oauth2/code/github")
    public ResponseEntity<String> github(HttpServletRequest request, HttpServletResponse response) {
        log.info(" ------- logged in -------- ");
        try {
            response.getWriter().write(" ------- Logged in To github ------ ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>("------- Logged in To github ------", HttpStatus.OK);
    }

}
