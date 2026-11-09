package com.authentication.application.service;

import com.authentication.application.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUserByEmail(String email);
    UserDto updateUser(UserDto userDto, String userId);
    void deleteUser(String id);
    UserDto getUserById(String id);
    Iterable<UserDto> getAllUsers();

}
