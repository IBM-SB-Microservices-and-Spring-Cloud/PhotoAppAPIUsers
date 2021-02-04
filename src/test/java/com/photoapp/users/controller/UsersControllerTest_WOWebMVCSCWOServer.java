package com.photoapp.users.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.photoapp.users.dto.UserDto;
import com.photoapp.users.service.UserServiceImpl;

/**
 * 
 * @author Abdul
 * 
 *         In this test, only webmvc Spring application context is instantiated
 *         instead of full spring context is started but without the server.
 *         
 *         https://spring.io/guides/gs/testing-web/
 *         
 */

@WebMvcTest
public class UsersControllerTest_WOWebMVCSCWOServer {

	@MockBean
	UserServiceImpl userService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testControllerGet() throws Exception {
		UserDto userDto = new UserDto();
		userDto.setUserId(String.valueOf(30));
		userDto.setName("MyUser");
		when(userService.getUser(String.valueOf(30))).thenReturn(userDto);
		mockMvc.perform(get("/users/{userId}", String.valueOf(30)).accept(MediaType.APPLICATION_JSON)).andDo(print())
				.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(30));

		verify(userService).getUser(String.valueOf(30));
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
