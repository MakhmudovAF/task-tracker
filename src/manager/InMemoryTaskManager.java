package manager;

import enums.Status;
import enums.Type;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId = 1;

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    protected final HistoryManager historyManager;

    protected final NavigableSet<Task> prioritizedTasks = new TreeSet<>((a, b) -> {
        LocalDateTime startTimeA = a.getStartTime();
        LocalDateTime startTimeB = b.getStartTime();

        if (startTimeA == null && startTimeB == null) return Integer.compare(a.getId(), b.getId());
        if (startTimeA == null) return 1;
        if (startTimeB == null) return -1;

        int cmp = startTimeA.compareTo(startTimeB);
        if (cmp != 0) return cmp;

        return Integer.compare(a.getId(), b.getId());
    });

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    // ---------- ID ----------
    private int generateId() {
        return nextId++;
    }

    // ---------- TASK ----------
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
            removeFromPrioritized(tasks.get(id));
        }
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Task createTask(Task task) {
        int id = generateId();
        task.setId(id);

        validateNoTimeIntersection(task, null);

        tasks.put(id, task);
        addToPrioritized(task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        if (task == null) return null;
        Task stored = tasks.get(task.getId());
        if (stored == null) return null;

        validateNoTimeIntersection(task, stored);

        removeFromPrioritized(stored);
        tasks.put(task.getId(), task);
        addToPrioritized(task);
        return task;
    }

    @Override
    public Task deleteTaskById(int id) {
        Task removed = tasks.remove(id);
        if (removed != null) {
            removeFromPrioritized(removed);
        }
        historyManager.remove(id);
        return removed;
    }

    // ---------- EPIC ----------
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
            removeFromPrioritized(subtasks.get(id));
        }
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        epics.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epic.setStatus(Status.NEW);
        epics.put(id, epic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic stored = epics.get(epic.getId());
        if (stored == null) {
            return null;
        }

        stored.setName(epic.getName());
        stored.setDescription(epic.getDescription());

        updateEpicStatus(stored.getId());
        calculateEpicEndTime(stored.getId());
        return stored;
    }

    @Override
    public Epic deleteEpicById(int id) {
        Epic removed = epics.remove(id);
        if (removed == null) {
            return null;
        }

        for (Integer subId : removed.getSubtaskIds()) {
            subtasks.remove(subId);
            historyManager.remove(subId);
        }

        historyManager.remove(id);
        return removed;
    }

    // ---------- SUBTASK ----------
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
            removeFromPrioritized(subtasks.get(id));
        }
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            return null;
        }

        int id = generateId();
        subtask.setId(id);
        validateNoTimeIntersection(subtask, null);
        subtasks.put(id, subtask);
        addToPrioritized(subtask);

        epic.addSubtaskId(id);
        updateEpicStatus(epic.getId());
        calculateEpicEndTime(epic.getId());
        return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask stored = subtasks.get(subtask.getId());
        if (stored == null) {
            return null;
        }

        if (stored.getEpicId() != subtask.getEpicId()) {
            return null;
        }

        validateNoTimeIntersection(subtask, stored);
        removeFromPrioritized(stored);

        subtasks.put(subtask.getId(), subtask);
        addToPrioritized(subtask);
        updateEpicStatus(subtask.getEpicId());
        calculateEpicEndTime(subtask.getEpicId());
        return subtask;
    }

    @Override
    public Subtask deleteSubtaskById(int id) {
        Subtask removed = subtasks.remove(id);
        if (removed == null) {
            return null;
        }
        removeFromPrioritized(removed);

        Epic epic = epics.get(removed.getEpicId());
        if (epic != null) {
            epic.removeSubtaskId(id);
            updateEpicStatus(epic.getId());
            calculateEpicEndTime(epic.getId());
        }

        historyManager.remove(id);
        return removed;
    }

    // ---------- EXTRA ----------
    @Override
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

    // ---------- HISTORY ----------
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    // ---------- STATUS ----------
    protected void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }

        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Integer subId : subtaskIds) {
            Subtask subtask = subtasks.get(subId);
            if (subtask == null) {
                continue;
            }

            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != Status.DONE) {
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

    protected void calculateEpicEndTime(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            epic.setDuration(0);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }

        long totalDuration = 0;
        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;

        for (Integer subId : subtaskIds) {
            Subtask subtask = subtasks.get(subId);
            if (subtask == null) {
                continue;
            }

            totalDuration += subtask.getDuration();

            LocalDateTime subtaskStart = subtask.getStartTime();
            LocalDateTime subtaskEnd = subtask.getEndTime();

            if (subtaskStart != null && (earliestStart == null || subtaskStart.isBefore(earliestStart))) {
                earliestStart = subtaskStart;
            }
            if (subtaskEnd != null && (latestEnd == null || subtaskEnd.isAfter(latestEnd))) {
                latestEnd = subtaskEnd;
            }
        }

        epic.setDuration(totalDuration);
        epic.setStartTime(earliestStart);
        epic.setEndTime(latestEnd);
    }

    // ---------------- TIME INTERSECTIONS ----------------
    private void addToPrioritized(Task task) {
        if (task == null) return;
        if (task.getType() == Type.EPIC) return;

        prioritizedTasks.add(task);
    }

    private void removeFromPrioritized(Task task) {
        if (task == null) return;
        if (task.getType() == Type.EPIC) return;

        prioritizedTasks.remove(task);
    }

    private void validateNoTimeIntersection(Task newTask, Task oldTask) {
        if (newTask == null) return;

        if (newTask.getStartTime() != null && newTask.getDuration() <= 0) {
            throw new IllegalArgumentException("duration must be > 0 when startTime is set");
        }

        if (newTask.getStartTime() == null || newTask.getDuration() <= 0) return;
        if (newTask.getType() == Type.EPIC) return;

        if (oldTask != null) prioritizedTasks.remove(oldTask);

        Task floor = prioritizedTasks.floor(newTask);
        Task ceil = prioritizedTasks.ceiling(newTask);

        if (floor != null && intersects(floor, newTask)) {
            if (oldTask != null) prioritizedTasks.add(oldTask);
            throw new IllegalArgumentException("Пересечение по времени с задачей id=" + floor.getId());
        }

        if (ceil != null && intersects(newTask, ceil)) {
            if (oldTask != null) prioritizedTasks.add(oldTask);
            throw new IllegalArgumentException("Пересечение по времени с задачей id=" + ceil.getId());
        }

        if (oldTask != null) prioritizedTasks.add(oldTask);
    }

    private boolean intersects(Task a, Task b) {
        if (a.getStartTime() == null || b.getStartTime() == null) return false;
        if (a.getEndTime() == null || b.getEndTime() == null) return false;

        return a.getStartTime().isBefore(b.getEndTime()) && b.getStartTime().isBefore(a.getEndTime());
    }
}