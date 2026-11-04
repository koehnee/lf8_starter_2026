package de.szut.lf8_starter.assignment;

import de.szut.lf8_starter.project.Project;
import jakarta.persistence.*;

@Entity
@Table(
        name = "project_assignment",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"project_id", "employee_id"}
        )
)
public class ProjectAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "qualification_id")
    private Long qualificationId;

    public ProjectAssignment() {
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getQualificationId() {
        return qualificationId;
    }

    public void setQualificationId(Long qualificationId) {
        this.qualificationId = qualificationId;
    }
}
