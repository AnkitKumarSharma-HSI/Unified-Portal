package com.dev.usersmanagementsystem.repository;

import com.dev.usersmanagementsystem.entity.ExecutionTime;
import com.dev.usersmanagementsystem.entity.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExecutionTimeRepo extends JpaRepository<ExecutionTime, Long>{
    List<ExecutionTime> findByStartTimeInMillis(Long currentTimeInMillis);

    @Query("SELECT e FROM ExecutionTime e WHERE e.scenarioId = :scenarioId AND e.userId = :userId")
    List<ExecutionTime> findExecutionTimeByScenarioIdAndUserId(@Param("scenarioId") int scenarioId, @Param("userId") int userId);

}
