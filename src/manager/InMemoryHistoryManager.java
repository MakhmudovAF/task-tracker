package manager;

import interfaces.HistoryManager;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();
    private static final int LIMIT_SIZE = 10;

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.size() >= LIMIT_SIZE) {
                history.remove(0);
            }
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(history);
    }
}
