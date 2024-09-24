package com.srinivas.userservice.services.Impl;

import com.srinivas.userservice.dtos.UserDto;
import com.srinivas.userservice.Models.Role;
import com.srinivas.userservice.repositories.RoleRepository;
import com.srinivas.userservice.repositories.UserRepository;
import com.srinivas.userservice.Models.User;
import com.srinivas.userservice.services.UserService;
import jakarta.persistence.Table;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }


    @Override
    public UserDto getUserDetails(Long userId) {
      Optional<User> optionalUser = userRepository.findById(userId);
      if(optionalUser.isEmpty()){
          return null;
      }
        return UserDto.from(optionalUser.get());
    }

    @Override
    public UserDto SetUserRoles(Long userId, List<Long> roleIds) {
        Optional<User> optionalUser = userRepository.findById(userId);
        List<Role> roles = roleRepository.findAllById(roleIds);
        if(optionalUser.isEmpty()){
            return null;
        }
        User user = optionalUser.get();
        user.setRoles(Set.copyOf(roles));
        User savedUser = userRepository.save(user);
        return UserDto.from(savedUser);
    }
}
