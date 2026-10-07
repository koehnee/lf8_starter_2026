package de.szut.lf8_starter.project;

import java.time.LocalDate;

public record ProjectResponse(
        Long id,
        String name,
        Long responsibleEmployeeId,
        Long customerId,
        String customerContact,
        String goalComment,
        LocalDate startDate,
        LocalDate plannedEndDate,
        LocalDate actualEndDate
) {}
