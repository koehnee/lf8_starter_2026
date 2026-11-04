package de.szut.lf8_starter.project;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssignmentRepository
        extends JpaRepository<ProjectAssignment, Long> {

    boolean existsByProject_IdAndEmployeeId(
            Long projectId,
            Long employeeId
    );

    List<ProjectAssignment> findByEmployeeId(
            Long employeeId
    );

    List<ProjectAssignment> findByProject_Id(
            Long projectId
    );
}