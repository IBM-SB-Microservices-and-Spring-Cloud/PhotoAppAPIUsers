package com.photoapp.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import com.photoapp.users.controller.UsersController;
import com.photoapp.users.dto.UserDto;
import com.photoapp.users.service.UserServiceImpl;

/**
 * 
 * @author Abdul
 *
 * This Class start a mock server
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UsersControllerMockTest {

	@MockBean
	UserServiceImpl userService;

	@InjectMocks
	UsersController usersController;

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testControllerGet() throws RestClientException, MalformedURLException {
		UserDto userDto = new UserDto();
		userDto.setUserId(String.valueOf(30));
		userDto.setName("MyUser");
		when(userService.getUser(String.valueOf(30))).thenReturn(userDto);
		ResponseEntity<UserDto> responseEntity = restTemplate
				.getForEntity(new URL("http://localhost:" + port + "/users/30").toString(), UserDto.class);
		UserDto userDto2 = responseEntity.getBody();
		System.out.println(userDto);
		System.out.println(userDto2);
		// If you don't have your own equals the below will fail.
		// Because default equals return (this == obj);
		assertTrue(userDto.equals(userDto2));
		verify(userService).getUser(String.valueOf(30));
		System.out.println("\n\n\n*** BODY " + userDto2 + "\n\n\n");
	}

	@Test
	public void testServiceGetVersion() {
		when(userService.getVersion()).thenReturn("V1");
		System.out.println("Going to invoke service.getUser");
		assertEquals("V1", userService.getVersion());

		/**
		 * When you use mock objects in unit test, you may also need no to verify in
		 * Mockito that the mock object had done specific methods. Verify in Mockito
		 * simply means that you want to check if a certain method of a mock object has
		 * been called by specific number of times. ... verify(mockObject).
		 */
		verify(userService).getVersion();
		// Test will fail if we have below lien, because getUser was not Invoked.
		// Message: Wanted but not invoked:
		// verify(userService).getUser("");
		System.out.println("Invoked service.getUser");
	}
}
