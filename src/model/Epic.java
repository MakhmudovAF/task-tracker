package model;

import java.util.List;

public class Epic extends Task {
    protected List<Integer> subtaskIds;

    public Epic(String name, String description, String status) {
        super(name, description, status);
    }
}