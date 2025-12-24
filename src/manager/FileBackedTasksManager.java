package manager;

import enums.TaskStatus;
import enums.TaskType;
import exception.ManagerSaveException;
import interfaces.HistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTasksManager(File file) {
        super(Managers.getDefaultHistory());
        this.file = file;
    }

    // ---------- SAVE ----------
    protected void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            StringBuilder sb = new StringBuilder();
            sb.append("id,type,name,status,description,epic\n");

            for (Task task : getTasks()) {
                sb.append(task).append("\n");
            }
            for (Epic epic : getEpics()) {
                sb.append(epic).append("\n");
            }
            for (Subtask subtask : getSubtasks()) {
                sb.append(subtask).append("\n");
            }

            sb.append("\n");
            sb.append(historyToString(historyManager));

            fileWriter.write(sb.toString());
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка сохранения файла", exception);
        }
    }

    // ---------- LOAD ----------
    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);

        try (BufferedReader bufferedFileReader = new BufferedReader(new FileReader(file))) {
            boolean isEmptyLine = false;
            while (bufferedFileReader.ready()) {
                String line = bufferedFileReader.readLine();
                if (line.equals("id,type,name,status,description,epic")) continue;
                if (line.isEmpty()) {
                    isEmptyLine = true;
                    continue;
                }
                if (isEmptyLine) {
                    List<Integer> history = historyFromString(line);
                    for (Integer id : history) {
                        Task task = manager.tasks.get(id);
                        if (task == null) task = manager.subtasks.get(id);
                        if (task == null) task = manager.epics.get(id);

                        if (task != null) {
                            manager.historyManager.add(task);
                        }
                    }
                    return manager;
                }

                Task task = fromString(line);
                manager.restore(task);
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка загрузки файла", exception);
        }

        return manager;
    }

    private void restore(Task task) {
        nextId = Math.max(nextId, task.getId());

        switch (task.getType()) {
            case TASK:
                tasks.put(task.getId(), task);
                break;
            case EPIC:
                epics.put(task.getId(), (Epic) task);
                break;
            case SUBTASK:
                Subtask sub = (Subtask) task;
                subtasks.put(sub.getId(), sub);
                epics.get(sub.getEpicId()).getSubtaskIds().add(sub.getId());
                break;
        }
    }

    // ---------- CSV ----------
    public static Task fromString(String value) {
        String[] parts = value.split(",");

        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];

        Task task;
        switch (type) {
            case TASK:
                task = new Task(name, description, status);
                break;
            case EPIC:
                task = new Epic(name, description);
                break;
            case SUBTASK:
                task = new Subtask(
                    name, description, status, Integer.parseInt(parts[5]));
                break;
            default:
                throw new IllegalArgumentException();
        }

        task.setId(id);
        return task;
    }

    public static String historyToString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        for (Task task : manager.getHistory()) {
            sb.append(task.getId()).append(",");
        }
        return sb.length() == 0 ? "" : sb.substring(0, sb.length() - 1);
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        if (value == null || value.isBlank()) return history;

        for (String id : value.split(",")) {
            history.add(Integer.parseInt(id));
        }
        return history;
    }

    // ---------- OVERRIDES ----------
    @Override
    public Task createTask(Task task) {
        Task t = super.createTask(task);
        save();
        return t;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic e = super.createEpic(epic);
        save();
        return e;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask s = super.createSubtask(subtask);
        save();
        return s;
    }

    @Override
    public Task getTask(int id) {
        Task t = super.getTask(id);
        save();
        return t;
    }

    @Override
    public Epic getEpic(int id) {
        Epic e = super.getEpic(id);
        save();
        return e;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask s = super.getSubtask(id);
        save();
        return s;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    // ---------- MAIN ----------
    public static void main(String[] args) {
        File file = new File("tasks.csv");

        FileBackedTasksManager manager = new FileBackedTasksManager(file);

        Task task = manager.createTask(
                new Task("Task", "Description", TaskStatus.NEW));

        Epic epic = manager.createEpic(
                new Epic("Epic", "Big task"));

        Subtask sub = manager.createSubtask(
                new Subtask("Sub", "Sub desc", TaskStatus.DONE, epic.getId()));

        manager.getTask(task.getId());
        manager.getEpic(epic.getId());
        manager.getSubtask(sub.getId());
        manager.getTask(task.getId());

        FileBackedTasksManager loaded = loadFromFile(file);

        System.out.println("История после восстановления:");
        for (Task task1 : loaded.getHistory()) {
            System.out.println(task1);
        }
    }
}
