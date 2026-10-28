package de.szut.lf8_starter.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import de.szut.lf8_starter.employee.EmployeeClient;
import de.szut.lf8_starter.employee.EmployeeDto;
import de.szut.lf8_starter.employee.EmployeeNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectServiceTest {

    private ProjectRepository projectRepository;
    private EmployeeClient employeeClient;
    private ProjectService service;

    @BeforeEach
    void setUp() {
        projectRepository = mock(ProjectRepository.class);
        employeeClient = mock(EmployeeClient.class);
        service = new ProjectService(projectRepository, new ProjectMapper(), employeeClient);
    }

    @Test
    void savesProjectForValidRequest() {
        // Arrange
        ProjectRequest request = validRequest();
        when(employeeClient.findById(1L)).thenReturn(Optional.of(new EmployeeDto(1L, "Anna", "Beispiel", null)));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project project = invocation.getArgument(0);
            project.setId(42L);
            return project;
        });

        // Act
        ProjectResponse response = service.createResponse(request);

        // Assert
        assertThat(response.id()).isEqualTo(42L);
        assertThat(response.name()).isEqualTo("Webshop");
        assertThat(response.responsibleEmployeeId()).isEqualTo(1L);
        assertThat(response.customerId()).isEqualTo(100L);
        assertThat(response.startDate()).isEqualTo(LocalDate.parse("2026-03-01"));
        assertThat(response.plannedEndDate()).isEqualTo(LocalDate.parse("2026-06-30"));
        assertThat(response.actualEndDate()).isNull();
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void doesNotSaveWhenEmployeeDoesNotExist() {
        // Arrange
        ProjectRequest request = validRequest();
        when(employeeClient.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.createResponse(request))
                .isInstanceOf(EmployeeNotFoundException.class);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void doesNotSaveWhenDateRangeIsInvalid() {
        // Arrange
        ProjectRequest request = new ProjectRequest(
                "Webshop",
                1L,
                100L,
                "Anna Beispiel",
                "Neuen Webshop entwickeln",
                LocalDate.parse("2026-06-30"),
                LocalDate.parse("2026-03-01"),
                null
        );
        when(employeeClient.findById(1L)).thenReturn(Optional.of(new EmployeeDto(1L, "Anna", "Beispiel", null)));

        // Act & Assert
        assertThatThrownBy(() -> service.createResponse(request))
                .isInstanceOf(InvalidProjectDateRangeException.class);
        verify(projectRepository, never()).save(any(Project.class));
    }

    private ProjectRequest validRequest() {
        return new ProjectRequest(
                "Webshop",
                1L,
                100L,
                "Anna Beispiel",
                "Neuen Webshop entwickeln",
                LocalDate.parse("2026-03-01"),
                LocalDate.parse("2026-06-30"),
                null
        );
    }
}
