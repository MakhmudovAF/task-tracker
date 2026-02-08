package interfaces;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    // ---------- TASK ----------
    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(int id);

    Task createTask(Task task);

    Task updateTask(Task task);

    Task deleteTaskById(int id);

    // ---------- EPIC ----------
    List<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicById(int id);

    Epic createEpic(Epic epic);

    Epic updateEpic(Epic epic);

    Epic deleteEpicById(int id);

    // ---------- SUBTASK ----------
    List<Subtask> getAllSubtasks();

    void deleteAllSubtasks();

    Subtask getSubtaskById(int id);

    Subtask createSubtask(Subtask subtask);

    Subtask updateSubtask(Subtask subtask);

    Subtask deleteSubtaskById(int id);

    // ---------- EXTRA ----------
    List<Subtask> getSubtasksOfEpic(int epicId);

    // ---------- HISTORY ----------
    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
