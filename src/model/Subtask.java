package model;

public class Subtask extends Task {
    protected int epicId;

    public Subtask(String name, String description, String status) {
        super(name, description, status);
    }
}