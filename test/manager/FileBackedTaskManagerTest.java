package manager;

import enums.Status;
import interfaces.TaskManager;
import interfaces.TaskManagerTest;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @TempDir
    Path tempDir;

    private Path file() {
        return tempDir.resolve("tasks.csv");
    }

    @Override
    protected FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(file());
    }

    // ---------- Доп. тесты по ТЗ для FileBacked ----------

    @Test
    void loadFromFile_shouldRestoreEmptyState_whenNoTasks() {
        Path path = file();

        TaskManager manager = new FileBackedTaskManager(path);
        // ничего не создаём, но можно принудительно создать файл через любую модифицирующую операцию:
        manager.deleteAllTasks();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(path);

        assertTrue(loaded.getAllTasks().isEmpty());
        assertTrue(loaded.getAllEpics().isEmpty());
        assertTrue(loaded.getAllSubtasks().isEmpty());
        assertTrue(loaded.getHistory().isEmpty());
    }

    @Test
    void loadFromFile_shouldRestoreEpicWithoutSubtasks() {
        Path path = file();

        FileBackedTaskManager manager = new FileBackedTaskManager(path);
        Epic epic = manager.createEpic(new Epic("E", "D"));

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(path);

        assertEquals(1, loaded.getAllEpics().size());
        assertNotNull(loaded.getEpicById(epic.getId()));
        assertTrue(loaded.getEpicById(epic.getId()).getSubtaskIds().isEmpty());
        assertEquals(Status.NEW, loaded.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void loadFromFile_shouldRestoreTasksAndHistory() {
        Path path = file();

        FileBackedTaskManager manager = new FileBackedTaskManager(path);

        Task t1 = manager.createTask(new Task("T1", "D1", Status.NEW));
        Epic e1 = manager.createEpic(new Epic("E1", "ED"));
        Subtask s1 = manager.createSubtask(new Subtask("S1", "SD", Status.IN_PROGRESS, e1.getId()));

        // просмотры -> история
        manager.getTaskById(t1.getId());
        manager.getEpicById(e1.getId());
        manager.getSubtaskById(s1.getId());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(path);

        assertNotNull(loaded.getTaskById(t1.getId()));
        assertNotNull(loaded.getEpicById(e1.getId()));
        assertNotNull(loaded.getSubtaskById(s1.getId()));

        assertEquals(3, loaded.getHistory().size());
        assertEquals(
                java.util.List.of(t1.getId(), e1.getId(), s1.getId()),
                loaded.getHistory().stream()
                        .map(Task::getId)
                        .collect(Collectors.toList()),
                "История должна восстановиться в правильном порядке"
        );
    }

    @Test
    void loadFromFile_shouldRestoreEmptyHistory_whenNoViews() {
        Path path = file();

        FileBackedTaskManager manager = new FileBackedTaskManager(path);
        manager.createTask(new Task("T1", "D1", Status.NEW));

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(path);

        assertTrue(loaded.getHistory().isEmpty(), "Если ничего не просматривали — история пустая");
    }

    @Test
    void loadFromFile_shouldRestoreDurationAndStartTime_forTaskAndSubtask() {
        Path path = file();
        FileBackedTaskManager manager = new FileBackedTaskManager(path);

        Task task = new Task("T", "D", Status.NEW);
        task.setDuration(45);
        task.setStartTime(LocalDateTime.of(2025, 2, 1, 10, 0));
        task = manager.createTask(task);

        Epic epic = manager.createEpic(new Epic("E", "D"));

        Subtask sub = new Subtask("S", "D", Status.IN_PROGRESS, epic.getId());
        sub.setDuration(30);
        sub.setStartTime(LocalDateTime.of(2025, 2, 1, 12, 0));
        sub = manager.createSubtask(sub);

        // Просмотры, чтобы сохранилась история (опционально)
        manager.getTaskById(task.getId());
        manager.getSubtaskById(sub.getId());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(path);

        Task loadedTask = loaded.getTaskById(task.getId());
        assertNotNull(loadedTask);
        assertEquals(45, loadedTask.getDuration());
        assertEquals(LocalDateTime.of(2025, 2, 1, 10, 0), loadedTask.getStartTime());
        assertEquals(LocalDateTime.of(2025, 2, 1, 10, 45), loadedTask.getEndTime());

        Subtask loadedSub = loaded.getSubtaskById(sub.getId());
        assertNotNull(loadedSub);
        assertEquals(30, loadedSub.getDuration());
        assertEquals(LocalDateTime.of(2025, 2, 1, 12, 0), loadedSub.getStartTime());
        assertEquals(LocalDateTime.of(2025, 2, 1, 12, 30), loadedSub.getEndTime());
    }

    @Test
    void loadFromFile_shouldRestoreEpicCalculatedFields_fromSubtasks() {
        Path path = file();
        FileBackedTaskManager manager = new FileBackedTaskManager(path);

        Epic epic = manager.createEpic(new Epic("E", "D"));

        Subtask s1 = new Subtask("S1", "D", Status.NEW, epic.getId());
        s1.setStartTime(LocalDateTime.of(2025, 3, 1, 9, 0));
        s1.setDuration(60);
        manager.createSubtask(s1);

        Subtask s2 = new Subtask("S2", "D", Status.NEW, epic.getId());
        s2.setStartTime(LocalDateTime.of(2025, 3, 1, 11, 0));
        s2.setDuration(15);
        manager.createSubtask(s2);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(path);

        Epic loadedEpic = loaded.getEpicById(epic.getId());
        assertNotNull(loadedEpic);

        assertEquals(75, loadedEpic.getDuration());
        assertEquals(LocalDateTime.of(2025, 3, 1, 9, 0), loadedEpic.getStartTime());
        assertEquals(LocalDateTime.of(2025, 3, 1, 11, 15), loadedEpic.getEndTime());
    }
}