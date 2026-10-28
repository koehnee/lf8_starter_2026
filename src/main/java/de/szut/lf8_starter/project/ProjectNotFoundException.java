package de.szut.lf8_starter.project;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(long id) {
        super("Project with ID " + id + " was not found.");
    }
}
