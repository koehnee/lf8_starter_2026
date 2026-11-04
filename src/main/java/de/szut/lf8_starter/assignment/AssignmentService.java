package de.szut.lf8_starter.assignment;

import de.szut.lf8_starter.project.Project;
import de.szut.lf8_starter.project.ProjectNotFoundException;
import de.szut.lf8_starter.project.ProjectRepository;
import org.springframework.stereotype.Service;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final ProjectRepository projectRepository;

    public AssignmentService(AssignmentRepository assignmentRepository, ProjectRepository projectRepository) {
        this.assignmentRepository = assignmentRepository;
        this.projectRepository = projectRepository;
    }

    public AssignmentResponse assignEmployee(long projectId, AssignEmployeeRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        long employeeId = request.employeeId();
        if (assignmentRepository.existsByProject_IdAndEmployeeId(projectId, employeeId)) {
            throw new DuplicateProjectAssignmentException(projectId, employeeId);
        }

        ProjectAssignment assignment = new ProjectAssignment();
        assignment.setProject(project);
        assignment.setEmployeeId(employeeId);
        assignment.setQualificationId(request.qualificationId());

        ProjectAssignment saved = assignmentRepository.save(assignment);
        return new AssignmentResponse(saved.getProject().getId(), saved.getEmployeeId(), saved.getQualificationId());
    }
}
