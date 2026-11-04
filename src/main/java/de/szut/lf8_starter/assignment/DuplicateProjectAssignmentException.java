package de.szut.lf8_starter.assignment;

public class DuplicateProjectAssignmentException extends RuntimeException {

    public DuplicateProjectAssignmentException(long projectId, long employeeId) {
        super("Mitarbeiter " + employeeId + " ist Projekt " + projectId + " bereits zugeordnet");
    }
}
