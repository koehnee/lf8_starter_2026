package de.szut.lf8_starter.employee;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * Ruft den lokalen Mitarbeiterdienst auf und reicht das JWT der eingehenden Anfrage weiter.
 * Weitere Aufrufe können nach dem Muster von {@link #findById(long)} ergänzt werden:
 * Token mitsenden und HTTP-Fehler gezielt in eigene Antworten übersetzen.
 */
@Component
public class EmployeeClient {

    private final RestClient client;

    public EmployeeClient(RestClient.Builder builder, @Value("${employee-service.url}") String baseUrl) {
        this.client = builder.baseUrl(baseUrl).build();
    }

    /** Sucht einen Mitarbeiter; eine unbekannte ID liefert Optional.empty(). */
    public Optional<EmployeeDto> findById(long id) {
        try {
            EmployeeDto employee = client.get()
                    .uri("/employees/{id}", id)
                    .headers(headers -> headers.setBearerAuth(currentToken()))
                    .retrieve()
                    .body(EmployeeDto.class);
            return Optional.ofNullable(employee);
        } catch (HttpClientErrorException.NotFound exception) {
            return Optional.empty();
        } catch (RestClientResponseException exception) {
            throw new EmployeeServiceUnavailableException(exception.getStatusCode(), exception);
        } catch (ResourceAccessException exception) {
            throw new EmployeeServiceUnavailableException(exception);
        }
    }

    public Optional<List<QualificationDto>> findQualificationsByEmployeeId(long id) {
        try {
            QualificationDto[] qualifications = client.get()
                    .uri("/employees/{id}/qualifications", id)
                    .headers(headers -> headers.setBearerAuth(currentToken()))
                    .retrieve()
                    .body(QualificationDto[].class);

            return Optional.ofNullable(qualifications)
                    .map(List::of);

        } catch (HttpClientErrorException.NotFound exception) {
            return Optional.empty();

        } catch (RestClientResponseException exception) {
            throw new EmployeeServiceUnavailableException(
                    exception.getStatusCode(), exception);

        } catch (ResourceAccessException exception) {
            throw new EmployeeServiceUnavailableException(exception);
        }
    }

    /** Nur innerhalb eines Requests mit JWT aufrufbar, nicht etwa aus einem CommandLineRunner. */
    private String currentToken() {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken jwt) {
            return jwt.getToken().getTokenValue();
        }
        throw new IllegalStateException("JWT zur Weitergabe fehlt");
    }
}
