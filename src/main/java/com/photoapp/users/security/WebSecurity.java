package com.photoapp.users.security;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.photoapp.users.service.UserServiceImpl;

//This is for method level security
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

	private Environment environment;
	UserServiceImpl userServiceImpl;

	BCryptPasswordEncoder brc;

	@Autowired
	public WebSecurity(Environment environment, UserServiceImpl userServiceImpl, BCryptPasswordEncoder brc) {
		super();
		this.environment = environment;
		this.userServiceImpl = userServiceImpl;
		this.brc = brc;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		String apigatewayip = environment.getProperty("api.gateway.ip");
		// We removed .antMatchers("/**").hasIpAddress(apigatewayip)
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/users").hasIpAddress(apigatewayip)
				.antMatchers("/h2-console/**").permitAll().
				anyRequest().authenticated().and()
				.addFilter(getAuthenticationFilter())
				.addFilter(new AuthorizationFilter(authenticationManager(), environment));
		http.headers().frameOptions().disable();
	}

	private Filter getAuthenticationFilter() throws Exception {
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(userServiceImpl, environment,
				authenticationManager());
		authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));
		return authenticationFilter;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(userServiceImpl).passwordEncoder(brc);

	}
}
