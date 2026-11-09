package com.authentication.application.exception;

import com.authentication.application.dto.ApiResponse;
import com.authentication.application.dto.ErrorRespose;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.function.EntityResponse;

import javax.security.auth.login.CredentialNotFoundException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.TimeZone;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorRespose> handleResourceNotFoundException(ResourceNotFoundException ex){

        ErrorRespose errorRespose = new ErrorRespose("404",ex.getMessage(),"Resource Not Found");
        return new ResponseEntity<>(errorRespose, HttpStatus.NOT_FOUND) ;
    }
    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseEntity<ErrorRespose> handleIllegalArgumentException(IllegalArgumentException ex){

        ErrorRespose errorRespose = new ErrorRespose(HttpStatus.BAD_REQUEST.toString(),ex.getMessage(),"Illegal Argument");
        return new ResponseEntity<>(errorRespose,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            UsernameNotFoundException.class,
            BadCredentialsException.class,
            CredentialNotFoundException.class,
            DisabledException.class
    })
    public ResponseEntity<ApiResponse> handleException(Exception ex, HttpServletRequest request){
        logger.info(ex.getMessage(),ex);
        ApiResponse error = ApiResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), "Bad Request", request.getRequestURI(), OffsetDateTime.now(ZoneOffset.UTC));

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
