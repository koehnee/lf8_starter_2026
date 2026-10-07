package de.szut.lf8_starter.project;

public class ProjectMapper {
    public Project fromRequest(ProjectRequest request) {
        Project entity = new Project();
        entity.setName(request.name());
        entity.setResponsibleEmployeeId(request.responsibleEmployeeId());
        entity.setCustomerId(request.customerId());
        entity.setCustomerContact(request.customerContact());
        entity.setGoalComment(request.goalComment());
        entity.setStartDate(request.startDate());
        entity.setPlannedEndDate(request.plannedEndDate());
        entity.setActualEndDate(request.actualEndDate());

        return entity;
    }

    public ProjectResponse toResponse(Project entity) {
        return new ProjectResponse(
                entity.getId(),
                entity.getName(),
                entity.getResponsibleEmployeeId(),
                entity.getCustomerId(),
                entity.getCustomerContact(),
                entity.getGoalComment(),
                entity.getStartDate(),
                entity.getPlannedEndDate(),
                entity.getActualEndDate()
        );
    }
}
