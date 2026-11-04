package de.szut.lf8_starter.assignment.exception;

import java.time.LocalDate;

public class EmployeeNotAvailableException extends RuntimeException {

    public EmployeeNotAvailableException(long employeeId, LocalDate startDate, LocalDate endDate) {
        super("Mitarbeiter " + employeeId + " ist im Zeitraum " + startDate + " bis " + endDate + " bereits verplant");
    }
}