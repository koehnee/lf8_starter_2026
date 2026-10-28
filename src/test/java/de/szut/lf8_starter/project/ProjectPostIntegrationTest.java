package de.szut.lf8_starter.project;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.ConnectException;
import java.util.List;
import java.util.Optional;

import de.szut.lf8_starter.AbstractIntegrationTest;
import de.szut.lf8_starter.employee.EmployeeDto;
import de.szut.lf8_starter.employee.EmployeeServiceUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class ProjectPostIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createReturns201() throws Exception {
        EmployeeDto employee = new EmployeeDto(1, "Max", "Mustermann", List.of());
        when(employeeClient.findById(1)).thenReturn(Optional.of(employee));
        String request = """
                {
                    "name": "Webshop",
                    "responsibleEmployeeId": 1,
                    "customerId": 100,
                    "customerContact": "Anna Beispiel",
                    "goalComment": "Neuen Webshop entwickeln",
                    "startDate": "2026-03-01",
                    "plannedEndDate": "2026-06-30",
                    "actualEndDate": "2026-06-15"
                }
                """;

        mockMvc.perform(post("/projects")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Webshop"))
                .andExpect(jsonPath("$.responsibleEmployeeId").value(1))
                .andExpect(jsonPath("$.customerId").value(100))
                .andExpect(jsonPath("$.customerContact").value("Anna Beispiel"))
                .andExpect(jsonPath("$.goalComment").value("Neuen Webshop entwickeln"))
                .andExpect(jsonPath("$.startDate").value("2026-03-01"))
                .andExpect(jsonPath("$.plannedEndDate").value("2026-06-30"))
                .andExpect(jsonPath("$.actualEndDate").value("2026-06-15"));
    }

    @Test
    void createWithBlankNameReturns400() throws Exception {
        String request = """
                {
                    "name": " ",
                    "responsibleEmployeeId": 1,
                    "customerId": 100,
                    "startDate": "2026-03-01",
                    "plannedEndDate": "2026-06-30"
                }
                """;

        mockMvc.perform(post("/projects")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    void createWithoutRequiredFieldsReturns400() throws Exception {
        mockMvc.perform(post("/projects")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.responsibleEmployeeId").exists())
                .andExpect(jsonPath("$.errors.customerId").exists())
                .andExpect(jsonPath("$.errors.startDate").exists())
                .andExpect(jsonPath("$.errors.plannedEndDate").exists());
    }

    @Test
    void createWithoutTokenReturns401() throws Exception {
        String request = """
                {
                    "name": "Webshop",
                    "responsibleEmployeeId": 1,
                    "customerId": 100,
                    "startDate": "2026-03-01",
                    "plannedEndDate": "2026-06-30"
                }
                """;

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createForUnknownEmployeeReturns404() throws Exception {
        when(employeeClient.findById(999)).thenReturn(Optional.empty());
        String request = """
                {
                    "name": "Webshop",
                    "responsibleEmployeeId": 999,
                    "customerId": 100,
                    "startDate": "2026-03-01",
                    "plannedEndDate": "2026-06-30"
                }
                """;

        mockMvc.perform(post("/projects")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Mitarbeiter 999 nicht gefunden"));
    }

    @Test
    void createWithInvalidDateRangeReturns422() throws Exception {
        EmployeeDto employee = new EmployeeDto(1, "Max", "Mustermann", List.of());
        when(employeeClient.findById(1)).thenReturn(Optional.of(employee));
        String request = """
                {
                    "name": "Webshop",
                    "responsibleEmployeeId": 1,
                    "customerId": 100,
                    "startDate": "2026-06-30",
                    "plannedEndDate": "2026-03-01"
                }
                """;

        mockMvc.perform(post("/projects")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail")
                        .value("Geplantes Enddatum darf nicht vor dem Startdatum liegen."));
    }

    @Test
    void createWhenEmployeeServiceIsUnavailableReturns503() throws Exception {
        when(employeeClient.findById(1))
                .thenThrow(new EmployeeServiceUnavailableException(new ConnectException()));
        String request = """
                {
                    "name": "Webshop",
                    "responsibleEmployeeId": 1,
                    "customerId": 100,
                    "startDate": "2026-03-01",
                    "plannedEndDate": "2026-06-30"
                }
                """;

        mockMvc.perform(post("/projects")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.detail")
                        .value("Employee-Service nicht erreichbar \u2013 l\u00e4uft docker compose?"));
    }
}