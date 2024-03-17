package com.srinivas.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateTokenRegisterDto {
    private String token;
    private Long userId;
}
