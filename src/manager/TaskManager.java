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

    // ---------------- SUBTASK ----------------
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            epic.setStatus(Status.NEW);
        }
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            return null;
        }

        int id = generateId();
        subtask.setId(id);
        subtasks.put(id, subtask);

        epic.addSubtaskId(id);
        updateEpicStatus(epic.getId());
        return subtask;
    }

    public Subtask updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            return null;
        }

        Subtask old = subtasks.get(subtask.getId());
        if (old.getEpicId() != subtask.getEpicId()) {
            return null;
        }

        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
        return subtask;
    }

    public Subtask deleteSubtaskById(int id) {
        Subtask removed = subtasks.remove(id);
        if (removed == null) {
            return null;
        }

        Epic epic = epics.get(removed.getEpicId());
        if (epic != null) {
            epic.removeSubtaskId(id);
            updateEpicStatus(epic.getId());
        }
        return removed;
    }

    // ---------------- EXTRA: subtasks of epic ----------------
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }
        List<Subtask> result = new ArrayList<>();
        for (Integer subId : epic.getSubtaskIds()) {
            Subtask st = subtasks.get(subId);
            if (st != null) {
                result.add(st);
            }
        }
        return result;
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