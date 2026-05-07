package com.krishnanityadas.task_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.krishnanityadas.task_management.model.User;
import com.krishnanityadas.task_management.repo.UserRepo;

@Service
public class CurrentUserService {
    @Autowired
    private UserRepo repo;

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder
            .getContext()
            .getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        return authentication.getName();
    }
    
    public User getCurrentUser() {
        String currentUsername = getCurrentUsername();
        User user = repo.findByUserName(currentUsername);
        if (user == null) {
            return new User();
        }
        return user;
    }

    public Boolean isUserAuthenticated(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return true;
        }
        return false;
    }
}
