package com.srinivas.userservice.repositories;

import com.srinivas.userservice.dtos.UserDto;
import com.srinivas.userservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
}
