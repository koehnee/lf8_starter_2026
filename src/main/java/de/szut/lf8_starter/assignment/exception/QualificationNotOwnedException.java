package de.szut.lf8_starter.assignment.exception;

public class QualificationNotOwnedException extends RuntimeException {

    public QualificationNotOwnedException(long employeeId, long qualificationId) {
        super("Mitarbeiter " + employeeId + " hat die Qualifikation " + qualificationId + " nicht");
    }
}