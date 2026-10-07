package de.szut.lf8_starter.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ProjectRequest(
        @NotBlank String name,
        @NotNull Long responsibleEmployeeId,
        @NotNull Long customerId,
        String customerContact,
        String goalComment,
        @NotNull LocalDate startDate,
        @NotNull LocalDate plannedEndDate,
        LocalDate actualEndDate) {
}
