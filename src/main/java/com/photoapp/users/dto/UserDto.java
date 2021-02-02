package com.photoapp.users.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	@NotNull(message = "Name should not be null")
	@Size(min = 3, message = "Name sould be >=3")
	private String name;

	@Email(message = "In valid email")
	@NotNull(message = "Email should not be null")
	private String email;

	private String password;
	private String userId;
	private List<AlbumsDTO> albums = new ArrayList<>();

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<AlbumsDTO> getAlbums() {
		return albums;
	}

	public void setAlbums(List<AlbumsDTO> albums) {
		this.albums = albums;
	}

}
