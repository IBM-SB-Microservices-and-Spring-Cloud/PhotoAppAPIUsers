package com.photoapp.users.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.photoapp.users.dto.AlbumsDTO;

import feign.FeignException;
import feign.hystrix.FallbackFactory;

@FeignClient(name = "albums-microservice", fallbackFactory = AlbumsFallbackFactory.class)
public interface AlbumsServiceClient {
	Logger logger = LoggerFactory.getLogger(AlbumsServiceClient.class);

	@GetMapping(path = "/users/{userId}/albums")
	public ResponseEntity<List<AlbumsDTO>> getAlbums(@PathVariable String userId);
}

@Component
class AlbumsFallbackFactory implements FallbackFactory<AlbumsServiceClient> {

	@Override
	public AlbumsServiceClient create(Throwable cause) {
		return new AlbumsServiceClientFallback(cause);
	}

}

class AlbumsServiceClientFallback implements AlbumsServiceClient {

	private final Throwable cause;

	public AlbumsServiceClientFallback(Throwable cause) {
		this.cause = cause;
	}

	@Override
	public ResponseEntity<List<AlbumsDTO>> getAlbums(String userId) {
		if (cause instanceof FeignException && ((FeignException) cause).status() == 404) {
			logger.error("404 error took place when getAlbums was called with userId: " + userId + ". Error message: "
					+ cause.getLocalizedMessage());
		} else {
			logger.error("Other error took place: " + cause.getLocalizedMessage());
		}
		return new ResponseEntity<List<AlbumsDTO>>(new ArrayList<AlbumsDTO>(), HttpStatus.OK);
	}

}