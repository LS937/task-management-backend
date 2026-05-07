package com.krishnanityadas.task_management.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.krishnanityadas.task_management.model.Task;

@Repository
public interface TaskRepo extends JpaRepository<Task, Integer> {

    List<Task> findAllByWorkspaceId(Integer workspaceId);

    //  findByWorkspaceId(Integer workspaceId);

    // Page<Task> findByWorkspaceIdAndStatus(Integer workspaceId, Status status, Pageable pageable);
}
