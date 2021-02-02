package com.photoapp.users.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.photoapp.users.dto.AlbumsDTO;
import com.photoapp.users.dto.UserDto;
import com.photoapp.users.dto.UserNotFoundException;
import com.photoapp.users.model.UserEntity;
import com.photoapp.users.model.UserRepo;

@Service
public class UserServiceImpl implements UserService {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	UserRepo userRepo;

//	@Autowired
//	@LoadBalanced
//	RestTemplate restTemplate;

	@Autowired
	AlbumsServiceClient albumsServiceClient;
	
	@Autowired
	Environment environment;

	@Override
	public UserDto createUser(@Valid UserDto userDto) {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
		userEntity.setEncryptedPass(bCryptPasswordEncoder.encode(userDto.getPassword()));
		userEntity.setUserId(userDto.getEmail() + new Random().nextInt());
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

	public ResponseEntity<UserDto> getUser(String userId) {
		Optional<UserEntity> userEntity = userRepo.findByUserId(userId);
		if (!userEntity.isPresent())
			throw new UserNotFoundException("No User Found with id: " + userId);
		UserDto userDto = modelMapper.map(userEntity.get(), UserDto.class);
		
		/*
		 String albumURL = environment.getProperty("albums.url");
		String url = String.format(albumURL, String.valueOf(userId));
		ResponseEntity<List<AlbumsDTO>> albumsResEntity = restTemplate.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<AlbumsDTO>>() {
				});
		List<AlbumsDTO> albums = albumsResEntity.getBody();
		*/
		
		logger.info("##### Before calling albumsServiceClient.getAlbums");
		ResponseEntity<List<AlbumsDTO>> albumsResEntity = albumsServiceClient.getAlbums(userId);
		logger.info("##### After calling albumsServiceClient.getAlbums");
		List<AlbumsDTO> albums = albumsResEntity.getBody();
		userDto.setAlbums(albums);
		return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
	}

}
