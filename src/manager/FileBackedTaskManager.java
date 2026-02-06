package manager;

import enums.Status;
import enums.Type;
import exception.ManagerSaveException;
import interfaces.HistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String HEADER = "id,type,name,status,description,epic";
    private final Path path;

    public FileBackedTaskManager(Path path) {
        super(Managers.getDefaultHistory());
        this.path = path;
    }

    // ---------- TASK ----------
    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save(); // просмотр меняет историю
        return task;
    }

    @Override
    public Task createTask(Task task) {
        Task created = super.createTask(task);
        save();
        return created;
    }

    @Override
    public Task updateTask(Task task) {
        Task updated = super.updateTask(task);
        if (updated != null) {
            save();
        }
        return updated;
    }

    @Override
    public Task deleteTaskById(int id) {
        Task removed = super.deleteTaskById(id);
        save();
        return removed;
    }

    // ---------- EPIC ----------
    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic created = super.createEpic(epic);
        save();
        return created;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updated = super.updateEpic(epic);
        if (updated != null) {
            save();
        }
        return updated;
    }

    @Override
    public Epic deleteEpicById(int id) {
        Epic removed = super.deleteEpicById(id);
        save();
        return removed;
    }

    // ---------- SUBTASK ----------
    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask created = super.createSubtask(subtask);
        save();
        return created;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updated = super.updateSubtask(subtask);
        if (updated != null) {
            save();
        }
        return updated;
    }

    @Override
    public Subtask deleteSubtaskById(int id) {
        Subtask removed = super.deleteSubtaskById(id);
        save();
        return removed;
    }

    // ---------- SAVE / LOAD ----------
    private void save() {
        try (Writer writer = new FileWriter(path.toFile())) {
            StringBuilder sb = new StringBuilder();
            sb.append(HEADER).append("\n");

            for (Task task : getAllTasks()) {
                sb.append(task).append("\n");
            }
            for (Epic epic : getAllEpics()) {
                sb.append(epic).append("\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                sb.append(subtask).append("\n");
            }

            sb.append("\n");
            sb.append(historyToString(historyManager));

            writer.write(sb.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла: " + path, e);
        }
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager manager = new FileBackedTaskManager(path);

        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            boolean readingHistory = false;
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.equals(HEADER)) {
                    continue;
                }

                if (line.isBlank()) {
                    readingHistory = true;
                    continue;
                }

                if (readingHistory) {
                    for (Integer id : historyFromString(line)) {
                        Task task = manager.tasks.get(id);
                        if (task == null) task = manager.epics.get(id);
                        if (task == null) task = manager.subtasks.get(id);

                        if (task != null) {
                            manager.historyManager.add(task);
                        }
                    }
                    return manager;
                }

                Task task = fromString(line);
                manager.restore(task);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки файла: " + path, e);
        }

        return manager;
    }

    private void restore(Task task) {
        nextId = Math.max(nextId, task.getId() + 1);

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

                Epic epic = epics.get(sub.getEpicId());
                if (epic == null) {
                    throw new ManagerSaveException("Подзадача " + sub.getId() +
                            " ссылается на несуществующий эпик " + sub.getEpicId(), null);
                }

                epic.addSubtaskId(sub.getId());
                updateEpicStatus(epic.getId());
                break;

            default:
                throw new IllegalArgumentException("Unknown type: " + task.getType());
        }
    }

    private static Task fromString(String line) {
        String[] parts = line.split(",", -1);

        int id = Integer.parseInt(parts[0]);
        Type type = Type.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        Task task;
        switch (type) {
            case TASK:
                task = new Task(name, description, status);
                break;

            case EPIC:
                task = new Epic(name, description);
                task.setStatus(status);
                break;

            case SUBTASK:
                int epic = Integer.parseInt(parts[5]);
                task = new Subtask(name, description, status, epic);
                break;

            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }

        task.setId(id);
        return task;
    }

    private static String historyToString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        for (Task task : manager.getHistory()) {
            sb.append(task.getId()).append(",");
        }
        return sb.length() == 0 ? "" : sb.substring(0, sb.length() - 1);
    }

    private static List<Integer> historyFromString(String line) {
        List<Integer> history = new ArrayList<>();
        if (line == null || line.isBlank()) return history;

        for (String id : line.split(",")) {
            history.add(Integer.parseInt(id));
        }
        return history;
    }

    public static void main(String[] args) {
        Path file = Path.of("tasks.csv");

        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task t1 = manager.createTask(new Task("Task1", "Desc1", Status.NEW));
        Epic e1 = manager.createEpic(new Epic("Epic1", "Epic desc"));
        Subtask s1 = manager.createSubtask(new Subtask("Sub1", "Sub desc", Status.IN_PROGRESS, e1.getId()));

        manager.getTaskById(t1.getId());
        manager.getEpicById(e1.getId());
        manager.getSubtaskById(s1.getId());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        System.out.println("=== LOADED TASKS ===");
        System.out.println(loaded.getAllTasks());

        System.out.println("\n=== LOADED EPICS ===");
        System.out.println(loaded.getAllEpics());

        System.out.println("\n=== LOADED SUBTASKS ===");
        System.out.println(loaded.getAllSubtasks());

        System.out.println("\n=== LOADED HISTORY ===");
        System.out.println(loaded.getHistory());
    }
}