package manager;

public class TaskManager {
    private int nextId = 1;

    private int generateId() {
        return nextId++;
    }
}