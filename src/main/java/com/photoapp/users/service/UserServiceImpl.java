package com.photoapp.users.service;

import java.util.ArrayList;
import java.util.Random;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.photoapp.users.dto.UserDto;
import com.photoapp.users.model.UserEntity;
import com.photoapp.users.model.UserRepo;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	UserRepo userRepo;

	@Override
	public UserDto createUser(@Valid UserDto userDto) {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
		userEntity.setEncryptedPass(bCryptPasswordEncoder.encode(userDto.getPassword()));
		userEntity.setUserId(userDto.getEmail()+new Random().nextInt());
		UserEntity returnEntity = userRepo.save(userEntity);
		UserDto retUDto = modelMapper.map(returnEntity, UserDto.class);
		return retUDto;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = userRepo.findByEmail(username);
		if (user == null)
			throw new UsernameNotFoundException(username);
		return new User(user.getEmail(), user.getEncryptedPass(), true, true, true, true, new ArrayList());
	}

	@Override
	public UserDto getUserByEmail(String email) {
		UserEntity user = userRepo.findByEmail(email);
		if (user == null)
			throw new UsernameNotFoundException(email);

		UserDto userDto = modelMapper.map(user, UserDto.class);
		return userDto;
	}

}
