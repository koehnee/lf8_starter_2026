package de.szut.lf8_starter.project;

public class InvalidProjectDateRangeException extends RuntimeException {

    public InvalidProjectDateRangeException() {
        super("Geplantes Enddatum darf nicht vor dem Startdatum liegen.");
    }
}
