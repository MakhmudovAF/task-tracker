package model;

import enums.Status;
import enums.Type;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    // CSV: id,type,name,status,description,epicId
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