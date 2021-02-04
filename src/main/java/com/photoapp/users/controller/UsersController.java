package com.photoapp.users.controller;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.photoapp.users.dto.UserDto;
import com.photoapp.users.service.UserService;

@RestController
@RequestMapping(value = "/users")
public class UsersController {

	@Autowired
	Environment env;

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	UserService userService;

	@GetMapping(value = "/version")
	public String getVersion() {
		String tokenSecret = env.getProperty("token.secret");
		return "v1. Running on Port: " + env.getProperty("local.server.port") + " tokenSecret: " + tokenSecret;
	}

	@PostMapping
	public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
		UserDto retUDto = userService.createUser(userDto);
		return new ResponseEntity<UserDto>(retUDto, HttpStatus.CREATED);
	}

	@GetMapping(path = "/{userId}")
	public ResponseEntity<UserDto> getUser(@PathVariable("userId") String userId) {
		UserDto userDto = userService.getUser(userId);
		return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
	}

}
