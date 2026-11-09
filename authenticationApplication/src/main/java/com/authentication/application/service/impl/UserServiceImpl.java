package com.authentication.application.service.impl;

import com.authentication.application.dto.UserDto;
import com.authentication.application.entity.Provider;
import com.authentication.application.entity.User;
import com.authentication.application.exception.ResourceNotFoundException;
import com.authentication.application.helper.UserHelper;
import com.authentication.application.repositories.UserRepository;
import com.authentication.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Override
    public UserDto createUser(UserDto userDto) {

        if(userDto.getEmail() == null || userDto.getEmail().isEmpty()){
            throw new IllegalArgumentException("Email is required");
        }
        if(userDto.getPassword() == null || userDto.getPassword().isEmpty()){
            throw new IllegalArgumentException("Password is required");
        }

        if(userRepository.findByEmail(userDto.getEmail()).isPresent()){
            throw new IllegalArgumentException("Email already exists.");
        }
        User user = modelMapper.map(userDto, User.class);
        user.setProvider(userDto.getProvider() != null ? userDto.getProvider() : Provider.LOCAL);
        user = userRepository.save(user);
        return modelMapper.map(user, UserDto.class) ;
    }

    @Override
    public UserDto getUserByEmail(String email) {
        if(!userRepository.existsByEmail(email)){
            throw new ResourceNotFoundException("User does not exists !!!");
        }
        return modelMapper.map(userRepository.findByEmail(email), UserDto.class) ;
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {

        User user = userRepository.findById(UserHelper.parseUUID(userId)).orElseThrow(() ->  new ResourceNotFoundException("User does not exists !!!") );
        // TODO: change the password chane logic.....
        if(userDto.getPassword() != null && !userDto.getPassword().isEmpty())
            user.setPassword(userDto.getPassword()) ;

        if(userDto.getName() != null)
            user.setName(userDto.getName()) ;

        if(userDto.getImage() != null)
            user.setImage(userDto.getImage()) ;

        if(userDto.getProvider() != null){
            user.setProvider(userDto.getProvider()) ;
        }

        user.setEnable(userDto.isEnable());

        userRepository.save(user);

        return null;
    }

    @Override
    public void deleteUser(String id) {
        UUID userId = UserHelper.parseUUID(id);

        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User does not exist with such id"));

        userRepository.deleteById(userId);
    }

    @Override
    public UserDto getUserById(String id) {

        User user = userRepository.findById(UserHelper.parseUUID(id)).orElseThrow(() -> new ResourceNotFoundException("User does not exist with such id"));

        return modelMapper.map(user, UserDto.class) ;
    }

    @Override
    public Iterable<UserDto> getAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .toList();
    }
}
