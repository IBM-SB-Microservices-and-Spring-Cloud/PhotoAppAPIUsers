package com.photoapp.users.service;

import javax.validation.Valid;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.photoapp.users.dto.UserDto;

public interface UserService extends UserDetailsService {

	UserDto createUser(@Valid UserDto userDto);

	UserDto getUserByEmail(String userName);

}
