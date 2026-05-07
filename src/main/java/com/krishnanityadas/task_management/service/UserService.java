package com.krishnanityadas.task_management.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.krishnanityadas.task_management.model.User;
import com.krishnanityadas.task_management.repo.UserRepo;

@Service
public class UserService {
    @Autowired
    private UserRepo repo;

    public User addUser(User user) {
        return repo.save(user);
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }
    
    // If you have a custom User entity
    // public Long getCurrentUserId() {
    //     UserDetails userDetails = getCurrentUser();
    //     if (userDetails instanceof UserPrincipal) {
    //         return ((UserPrincipal) userDetails).getId();
    //     }
    //     return null;
    // }
}

