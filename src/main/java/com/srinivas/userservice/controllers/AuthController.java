package com.srinivas.userservice.controllers;

import com.srinivas.userservice.dtos.*;
import com.srinivas.userservice.models.SessionStatus;
import com.srinivas.userservice.services.Impl.AuthServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthServiceImpl authService;
    public AuthController(AuthServiceImpl authService){
        this.authService= authService;
    }


    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDTO loginRequestDTO){
        return authService.login(loginRequestDTO.getEmail(),loginRequestDTO.getPassword());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void>logout(@RequestBody LogoutRequestDTO logoutRequestDTO){
        return authService.logout(logoutRequestDTO.getToken(),logoutRequestDTO.getUserId());
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto>signUp(@RequestBody SignUpRequestDto signUpRequestDto){
        UserDto userDto = authService.signUp(signUpRequestDto.getEmail(), signUpRequestDto.getEmail());
        return new ResponseEntity<>(userDto,HttpStatus.OK);
    }

    @PostMapping("/validate")
    public ResponseEntity<SessionStatus>validate(@RequestBody ValidateTokenRegisterDto validateTokenRegisterDto){
        SessionStatus sessionStatus = authService.validate(validateTokenRegisterDto.getToken(),validateTokenRegisterDto.getUserId());
        return new ResponseEntity<>(sessionStatus,HttpStatus.OK);
    }


}
