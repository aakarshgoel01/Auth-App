package com.authentication.application.service;

import com.authentication.application.dto.UserDto;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthService {

    public UserDto registerUser(@RequestBody UserDto userDto);

}
