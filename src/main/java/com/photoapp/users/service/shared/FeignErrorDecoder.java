package com.photoapp.users.service.shared;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import feign.Response;
import feign.codec.ErrorDecoder;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

	Environment environment;

	@Autowired
	public FeignErrorDecoder(Environment environment) {
		super();
		this.environment = environment;
	}

	@Override
	public Exception decode(String methodKey, Response response) {
		switch (response.status()) {
		case 400:
			// return new BadRequestException();
			break;
		case 404: {
			if (methodKey.contains("getAlbums")) {
				String aeanotfound = environment.getProperty("albums.exception.albums_not_found");
				return new ResponseStatusException(HttpStatus.valueOf(response.status()), aeanotfound);
			}
			return new ResponseStatusException(HttpStatus.NOT_FOUND, response.reason());
		}
		default:
			break;
		}
		return null;
	}

}
