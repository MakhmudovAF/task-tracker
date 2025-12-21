import model.Epic;
import model.Subtask;
import model.Task;

import java.util.*;

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

    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubtasks();
    }

    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) return epics.get(id);
        return null;
    }

    public void createEpic(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            List<Integer> existingSubtaskIds = epics.get(epic.getId()).getSubtaskIds();
            epic.getSubtaskIds().clear();
            epic.getSubtaskIds().addAll(existingSubtaskIds);
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic.getId());
        }
    }

    private void updateEpicStatus(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }

        List<Subtask> epicSubtasks = getSubtasksByEpicId(id);

        if (epicSubtasks.isEmpty()) {
            epic.setStatus("NEW");
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Subtask subtask : epicSubtasks) {
            if (!subtask.getStatus().equals("NEW")) {
                allNew = false;
            }
            if (!subtask.getStatus().equals("DONE")) {
                allDone = false;
            }
        }

        if (allNew) {
            epic.setStatus("NEW");
        } else if (allDone) {
            epic.setStatus("DONE");
        } else {
            epic.setStatus("IN_PROGRESS");
        }
    }

    private List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return Collections.emptyList();
        }

        List<Subtask> result = new ArrayList<>();
        for (int subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                result.add(subtask);
            }
        }
        return result;
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    // ========== Методы для подзадач ==========

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }

    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) return subtasks.get(id);
        return null;
    }

    public void createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        subtask.setId(getNextId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(subtask.getEpicId());
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic == null) {
                return;
            }

            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(subtask.getEpicId());
            }
            subtasks.remove(id);
        }
    }
}