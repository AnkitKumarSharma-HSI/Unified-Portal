package com.dev.usersmanagementsystem.repository;

import com.dev.usersmanagementsystem.entity.ExecutionTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExecutionTimeRepo extends JpaRepository<ExecutionTime, Long>{
    List<ExecutionTime> findByStartTimeInMillis(Long currentTimeInMillis);
}
