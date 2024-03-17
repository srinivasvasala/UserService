package com.srinivas.userservice.services;

import com.srinivas.userservice.dtos.UserDto;
import com.srinivas.userservice.models.SessionStatus;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<UserDto>login(String email,String password);
    ResponseEntity<Void>logout(String token,Long userId);
    UserDto signUp(String email,String password);
    SessionStatus validate(String token,Long userId);

}
