package com.krishnanityadas.task_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.krishnanityadas.task_management.model.User;
import com.krishnanityadas.task_management.model.UserPrincipal;
import com.krishnanityadas.task_management.repo.UserRepo;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo repo;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repo.findByUserName(username);
        if (user == null) {
            System.out.println(404);
            throw new UsernameNotFoundException("User not found 404");
        }
        return new UserPrincipal(user);
    }
    
}
