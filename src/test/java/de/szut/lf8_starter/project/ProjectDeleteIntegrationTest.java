package de.szut.lf8_starter.project;

import de.szut.lf8_starter.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectDeleteIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void deleteReturns204AndRemovesProject() throws Exception {
        Project project = new Project();
        project.setName("Zu löschen");
        project.setResponsibleEmployeeId(1L);
        project.setCustomerId(100L);
        project.setStartDate(LocalDate.parse("2026-03-01"));
        project.setPlannedEndDate(LocalDate.parse("2026-06-30"));
        Project saved = projectRepository.save(project);

        mockMvc.perform(delete("/projects/{projectId}", saved.getId()).with(jwt()))
                .andExpect(status().isNoContent());

        assertThat(projectRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void deleteUnknownProjectReturns404() throws Exception {
        mockMvc.perform(delete("/projects/{projectId}", -1).with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteWithoutTokenReturns401() throws Exception {
        mockMvc.perform(delete("/projects/{projectId}", -1))
                .andExpect(status().isUnauthorized());
    }
}
