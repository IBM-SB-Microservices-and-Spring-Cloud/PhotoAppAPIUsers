package com.photoapp.users.security;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.photoapp.users.dto.UserDto;
import com.photoapp.users.model.LoginReqModel;
import com.photoapp.users.service.UserService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private UserService usersService;
	private Environment environment;
	
	public AuthenticationFilter(UserService usersService, 
			Environment environment, 
			AuthenticationManager authenticationManager) {
		this.usersService = usersService;
		this.environment = environment;
		super.setAuthenticationManager(authenticationManager);
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		try {
			LoginReqModel creds = new ObjectMapper().readValue(request.getInputStream(), LoginReqModel.class);
			return getAuthenticationManager().authenticate(
					new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList()));
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain fc,
			Authentication auth) throws IOException, ServerException {
		String userName = ((User) auth.getPrincipal()).getUsername();
    	UserDto userDto = usersService.getUserByEmail(userName);
    	//hfgry463hf746hf573ydh475fhy5739
    	String tokenSecret = environment.getProperty("token.secret");
        String token = Jwts.builder()
                .setSubject(userDto.getUserId())
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512,  tokenSecret)
                .compact();
        
        response.addHeader("token", token);
        response.addHeader("userId", userDto.getUserId());
	}
}
