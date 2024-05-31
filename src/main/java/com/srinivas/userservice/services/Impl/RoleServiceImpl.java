package com.srinivas.userservice.services.Impl;

import com.srinivas.userservice.Models.Role;
import com.srinivas.userservice.repositories.RoleRepository;
import com.srinivas.userservice.services.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private  RoleRepository roleRepository;
    public RoleServiceImpl(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }
    @Override
    public Role createRole(String name) {
        Role role = new Role();
        role.setRole(name);
        return roleRepository.save(role);
    }
}
