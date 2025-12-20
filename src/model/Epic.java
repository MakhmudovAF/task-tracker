package model;

import java.util.List;

public class Epic extends Task {
    protected List<Integer> subtaskIds;

    public Epic(String name, String description, int id, String status) {
        super(name, description, id, status);
    }
}