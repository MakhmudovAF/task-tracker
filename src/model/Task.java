package model;

import enums.TaskStatus;
import enums.TaskType;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return id + "," +
                getType() + "," +
                name + "," +
                status + "," +
                description + ",";
    }
}