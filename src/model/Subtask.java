package model;

import enums.TaskStatus;
import enums.TaskType;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return id + "," +
                getType() + "," +
                name + "," +
                status + "," +
                description + "," +
                epicId;
    }
}