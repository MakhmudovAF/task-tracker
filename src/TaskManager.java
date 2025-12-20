import model.Epic;
import model.Subtask;
import model.Task;

import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Epic> epics;
    private Map<Integer, Subtask> subtasks;
    private int nextId;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.nextId = 1;
    }

    private int getNextId() {
        return nextId++;
    }

    // ========== Методы для простых задач ==========

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) return tasks.get(id);
        return null;
    }

    public void createTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    // ========== Методы для эпиков ==========

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    // ========== Методы для подзадач ==========

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }


}