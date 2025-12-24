package interfaces;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;
import java.util.Map;

public interface TaskManager {
    Task createTask(Task task);
    List<Task> getTasks();
    Task getTask(int id);
    void updateTask(Task task);
    void deleteTask(int id);
    void deleteAllTasks();

    Epic createEpic(Epic epic);
    List<Epic> getEpics();
    Epic getEpic(int id);
    void deleteEpic(int id);
    void deleteAllEpics();

    Subtask createSubtask(Subtask subtask);
    List<Subtask> getSubtasks();
    Subtask getSubtask(int id);
    void updateSubtask(Subtask subtask);
    void deleteSubtask(int id);
    List<Subtask> getEpicSubtasks(int epicId);

    List<Task> getHistory();
}