package de.szut.lf8_starter.assignment;

import de.szut.lf8_starter.assignment.exception.DuplicateProjectAssignmentException;
import de.szut.lf8_starter.assignment.exception.EmployeeNotAvailableException;
import de.szut.lf8_starter.assignment.exception.QualificationNotOwnedException;
import de.szut.lf8_starter.employee.EmployeeClient;
import de.szut.lf8_starter.employee.EmployeeNotFoundException;
import de.szut.lf8_starter.project.Project;
import de.szut.lf8_starter.project.ProjectNotFoundException;
import de.szut.lf8_starter.project.ProjectRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeClient employeeClient;

    public AssignmentService(
            AssignmentRepository assignmentRepository,
            ProjectRepository projectRepository,
            EmployeeClient employeeClient
    ) {
        this.assignmentRepository = assignmentRepository;
        this.projectRepository = projectRepository;
        this.employeeClient = employeeClient;
    }

    public AssignmentResponse assignEmployee(long projectId, AssignEmployeeRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        long employeeId = request.employeeId();
        employeeClient.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        if (assignmentRepository.existsByProject_IdAndEmployeeId(projectId, employeeId)) {
            throw new DuplicateProjectAssignmentException(projectId, employeeId);
        }

        long qualificationId = request.qualificationId();
        boolean hasQualification = employeeClient.findQualificationsByEmployeeId(employeeId)
                .orElse(List.of())
                .stream()
                .anyMatch(qualification -> qualification.id() == qualificationId);
        if (!hasQualification) {
            throw new QualificationNotOwnedException(employeeId, qualificationId);
        }

        LocalDate startDate = project.getStartDate();
        LocalDate endDate = project.getPlannedEndDate();
        boolean employeeAlreadyPlanned = assignmentRepository.findByEmployeeId(employeeId)
                .stream()
                .map(ProjectAssignment::getProject)
                .anyMatch(existingProject ->
                        !startDate.isAfter(existingProject.getPlannedEndDate())
                                && !existingProject.getStartDate().isAfter(endDate)
                );
        if (employeeAlreadyPlanned) {
            throw new EmployeeNotAvailableException(employeeId, startDate, endDate);
        }

        ProjectAssignment assignment = new ProjectAssignment();
        assignment.setProject(project);
        assignment.setEmployeeId(employeeId);
        assignment.setQualificationId(qualificationId);

        ProjectAssignment saved = assignmentRepository.save(assignment);
        return new AssignmentResponse(saved.getProject().getId(), saved.getEmployeeId(), saved.getQualificationId());
    }
}
