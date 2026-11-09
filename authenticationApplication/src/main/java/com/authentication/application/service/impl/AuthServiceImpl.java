package com.authentication.application.service.impl;

import com.authentication.application.dto.UserDto;
import com.authentication.application.service.AuthService;
import com.authentication.application.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private UserService userService;
    private PasswordEncoder passwordEncoder;
    @Override
    public UserDto registerUser(UserDto userDto) {

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userService.createUser(userDto);
    }
}
