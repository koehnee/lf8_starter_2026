package de.szut.lf8_starter.project;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import de.szut.lf8_starter.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

class ProjectGetIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository repository;

    @Test
    void getAllReturns200AndAllProjects() throws Exception {
        // Arrange
        repository.save(createProject("Webshop"));
        repository.save(createProject("Mobile App"));

        // Act & Assert
        mockMvc.perform(get("/projects").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Webshop"))
                .andExpect(jsonPath("$[1].name").value("Mobile App"));
    }

    @Test
    void getAllWithoutProjectsReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/projects").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/projects"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getByIdReturns200() throws Exception {
        Project saved = repository.save(createProject("Webshop"));

        mockMvc.perform(get("/projects/{id}", saved.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Webshop"))
                .andExpect(jsonPath("$.responsibleEmployeeId").value(1))
                .andExpect(jsonPath("$.customerId").value(100));
    }

    @Test
    void getByIdUnknownReturns404() throws Exception {
        mockMvc.perform(get("/projects/999999").with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByIdWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/projects/1"))
                .andExpect(status().isUnauthorized());
    }

    private Project createProject(String name) {
        Project project = new Project();
        project.setName(name);
        project.setResponsibleEmployeeId(1L);
        project.setCustomerId(100L);
        project.setStartDate(LocalDate.of(2026, 3, 1));
        project.setPlannedEndDate(LocalDate.of(2026, 6, 30));
        return project;
    }
}
