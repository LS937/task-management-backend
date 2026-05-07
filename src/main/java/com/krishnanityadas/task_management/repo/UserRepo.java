package com.krishnanityadas.task_management.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.krishnanityadas.task_management.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Integer>{
    public User findByUserName(String userName);
    public User findByEmail(String email);
    public boolean existsByUserName(String userName);
}
