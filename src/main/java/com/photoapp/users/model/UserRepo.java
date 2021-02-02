package com.photoapp.users.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, String> {

		UserEntity findByEmail(String email);

		Optional<UserEntity> findByUserId(String userId);
}
