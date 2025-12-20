package model;

public class Subtask extends Task {
    protected int epicId;

    public Subtask(String name, String description, int id, String status) {
        super(name, description, id, status);
    }
}