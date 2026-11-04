package de.szut.lf8_starter.assignment;

import de.szut.lf8_starter.project.Project;
import de.szut.lf8_starter.project.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AssignmentServiceTest {

    private AssignmentRepository assignmentRepository;
    private ProjectRepository projectRepository;
    private AssignmentService service;

    @BeforeEach
    void setUp() {
        assignmentRepository = mock(AssignmentRepository.class);
        projectRepository = mock(ProjectRepository.class);
        service = new AssignmentService(assignmentRepository, projectRepository);
    }

    @Test
    void assignEmployeeThrowsConflictWhenDuplicateExists() {
        long projectId = 7L;
        long employeeId = 11L;

        Project project = new Project();
        project.setId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(assignmentRepository.existsByProject_IdAndEmployeeId(projectId, employeeId)).thenReturn(true);

        AssignEmployeeRequest request = new AssignEmployeeRequest(employeeId, 22L);

        assertThatThrownBy(() -> service.assignEmployee(projectId, request))
                .isInstanceOf(DuplicateProjectAssignmentException.class);
        verify(assignmentRepository, never()).save(org.mockito.ArgumentMatchers.any(ProjectAssignment.class));
    }
}
