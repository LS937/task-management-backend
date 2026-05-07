package com.krishnanityadas.task_management.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.krishnanityadas.task_management.model.Workspace;

@Repository
public interface WorkspaceRepo extends JpaRepository<Workspace, Integer>{

}
