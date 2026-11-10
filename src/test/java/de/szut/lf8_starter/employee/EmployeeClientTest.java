
package de.szut.lf8_starter.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

import java.net.ConnectException;
import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class EmployeeClientTest {

    private MockRestServiceServer server;
    private EmployeeClient client;

    @BeforeEach
    void setup() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new EmployeeClient(builder, "http://localhost:8089");
        Jwt jwt = new Jwt("student-token", Instant.now(), Instant.now().plusSeconds(3600),
                Map.of("alg", "none"), Map.of("sub", "student"));
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
        server.verify();
    }

    @Test
    void forwardsTokenAndMapsSkills() {
        server.expect(requestTo("http://localhost:8089/employees/1"))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer student-token"))
                .andRespond(withSuccess("""
                        {"id":1,"firstName":"Max","lastName":"Mustermann",
                         "skillSet":[{"id":2,"skill":"Java"}],"extra":true}
                        """, MediaType.APPLICATION_JSON));

        EmployeeDto employee = client.findById(1).orElseThrow();

        assertThat(employee.skillSet()).containsExactly(new QualificationDto(2L, "Java"));
        assertThat(employee.firstName()).isEqualTo("Max");
    }

    @Test
    void maps401ToUnavailableWithStatus() {
        server.expect(requestTo("http://localhost:8089/employees/1"))
                .andRespond(withUnauthorizedRequest());
        assertThatThrownBy(() -> client.findById(1))
                .isInstanceOf(EmployeeServiceUnavailableException.class)
                .hasMessageContaining("401");
    }

    @Test
    void maps500ToUnavailableWithStatus() {
        server.expect(requestTo("http://localhost:8089/employees/1"))
                .andRespond(withServerError());
        assertThatThrownBy(() -> client.findById(1))
                .isInstanceOf(EmployeeServiceUnavailableException.class)
                .hasMessageContaining("500");
    }

    @Test
    void mapsConnectionFailureToUnavailable() {
        server.expect(requestTo("http://localhost:8089/employees/1"))
                .andRespond(withException(new ConnectException("Verbindung verweigert")));
        assertThatThrownBy(() -> client.findById(1))
                .isInstanceOf(EmployeeServiceUnavailableException.class)
                .hasMessageContaining("nicht erreichbar");
    }

    @Test
    void maps404ToEmpty() {
        server.expect(requestTo("http://localhost:8089/employees/999"))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer student-token"))
                .andRespond(withResourceNotFound());

        var employee = client.findById(999);

        assertThat(employee).isEmpty();
    }

    @Test
    void findByIdWithQualificationsForwardsTokenAndMapsSkills() {
        server.expect(requestTo("http://localhost:8089/employees/1"))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer student-token"))
                .andRespond(withSuccess("""
                        {"id":1,"firstName":"Max","lastName":"Mustermann",
                         "skillSet":[{"id":2,"skill":"Java"}],"department":"IT"}
                        """, MediaType.APPLICATION_JSON));

        EmployeeDto employee = client.findQualificationsByEmployeeId(1);

        assertThat(employee.id()).isEqualTo(1L);
        assertThat(employee.skillSet()).containsExactly(new QualificationDto(2L, "Java"));
    }

    @Test
    void findByIdWithQualificationsMaps404ToNotFound() {
        server.expect(requestTo("http://localhost:8089/employees/999"))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer student-token"))
                .andRespond(withResourceNotFound());

        assertThatThrownBy(() -> client.findQualificationsByEmployeeId(999))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void findByIdWithQualificationsMapsConnectionFailureToUnavailable() {
        server.expect(requestTo("http://localhost:8089/employees/1"))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer student-token"))
                .andRespond(withException(new ConnectException("Verbindung verweigert")));

        assertThatThrownBy(() -> client.findQualificationsByEmployeeId(1))
                .isInstanceOf(EmployeeServiceUnavailableException.class)
                .hasMessageContaining("nicht erreichbar");
    }
}