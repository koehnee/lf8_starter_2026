package de.szut.lf8_starter.project;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Project {
    @Id
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
