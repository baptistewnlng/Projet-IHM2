package com.projetihm;

import javafx.scene.input.DataFormat;
import java.time.LocalDate;

public class Task {
    public static final DataFormat DATA_FORMAT = new DataFormat("application/task");

    public enum Status {
        TODO, IN_PROGRESS, COMPLETED
    }

    private String title;
    private String description;
    private LocalDate dueDate;
    private Status status;

    public Task(String title, String description, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = Status.TODO;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return title;
    }
}
