package de.szut.lf8_starter.assignment;

import jakarta.validation.constraints.NotNull;

public record AssignEmployeeRequest(
        @NotNull Long employeeId,
        @NotNull Long qualificationId
) {
}
