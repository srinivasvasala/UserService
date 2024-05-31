package com.srinivas.userservice.services;

import com.srinivas.userservice.dtos.UserDto;

import java.util.List;

public interface UserService {
    public UserDto getUserDetails(Long userId);
    public UserDto SetUserRoles(Long userId, List<Long> roleIds);

}
