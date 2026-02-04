package manager;

import enums.Status;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private int nextId = 1;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    private int generateId() {
        return nextId++;
    }

    // ---------------- TASK ----------------
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Task createTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    public Task updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return null;
        }
        tasks.put(task.getId(), task);
        return task;
    }

    public Task deleteTaskById(int id) {
        return tasks.remove(id);
    }

    // ---------------- EPIC ----------------
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Epic createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epic.setStatus(Status.NEW);
        epics.put(id, epic);
        return epic;
    }

    public Epic updateEpic(Epic epic) {
        Epic stored = epics.get(epic.getId());
        if (stored == null) {
            return null;
        }

        stored.setName(epic.getName());
        stored.setDescription(epic.getDescription());

        updateEpicStatus(stored.getId());
        return stored;
    }

    public Epic deleteEpicById(int id) {
        Epic removed = epics.remove(id);
        if (removed == null) {
            return null;
        }
        for (Integer subId : removed.getSubtaskIds()) {
            subtasks.remove(subId);
        }
        return removed;
    }

    // ---------------- STATUS CALC ----------------
    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }

        List<Integer> ids = epic.getSubtaskIds();
        if (ids.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Integer subId : ids) {
            Subtask st = subtasks.get(subId);
            if (st == null) {
                continue;
            }
            if (st.getStatus() != Status.NEW) {
                allNew = false;
            }
            if (st.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}