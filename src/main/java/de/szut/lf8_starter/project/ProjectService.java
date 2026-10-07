package de.szut.lf8_starter.project;

import de.szut.lf8_starter.employee.EmployeeClient;
import de.szut.lf8_starter.employee.EmployeeNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final EmployeeClient employeeClient;

    public ProjectService(ProjectRepository projectRepository, ProjectMapper projectMapper, EmployeeClient employeeClient) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.employeeClient = employeeClient;
    }

    public ProjectResponse createResponse(ProjectRequest request) {
        long responsibleEmployeeId = request.responsibleEmployeeId();
        employeeClient.findById(responsibleEmployeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(responsibleEmployeeId));

        if (request.plannedEndDate().isBefore(request.startDate())) {
            throw new InvalidProjectDateRangeException();
        }

        Project entity = projectMapper.fromRequest(request);
        Project savedEntity = projectRepository.save(entity);
        return projectMapper.toResponse(savedEntity);
    }
}
