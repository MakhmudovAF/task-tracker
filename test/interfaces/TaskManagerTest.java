package interfaces;

import enums.Status;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    protected abstract T createManager();

    @BeforeEach
    void setUp() {
        manager = createManager();
    }

    // ---------- TASK ----------
    @Test
    void createTask_shouldCreateAndReturnWithId() {
        Task created = manager.createTask(new Task("T", "D", Status.NEW));
        assertNotNull(created);
        assertTrue(created.getId() > 0);

        Task loaded = manager.getTaskById(created.getId());
        assertNotNull(loaded);
        assertEquals(created.getId(), loaded.getId());
    }

    @Test
    void getAllTasks_shouldBeEmptyInitially() {
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    void getTaskById_shouldReturnNullForWrongId() {
        assertNull(manager.getTaskById(999));
    }

    @Test
    void updateTask_shouldUpdateExisting() {
        Task created = manager.createTask(new Task("T", "D", Status.NEW));

        Task updated = new Task(created.getId(), "T2", "D2", Status.DONE);
        Task result = manager.updateTask(updated);

        assertNotNull(result);
        assertEquals("T2", manager.getTaskById(created.getId()).getName());
        assertEquals(Status.DONE, manager.getTaskById(created.getId()).getStatus());
    }

    @Test
    void updateTask_shouldReturnNullForWrongId() {
        Task updated = new Task(999, "T2", "D2", Status.DONE);
        assertNull(manager.updateTask(updated));
    }

    @Test
    void deleteTaskById_shouldRemoveTask() {
        Task created = manager.createTask(new Task("T", "D", Status.NEW));

        Task removed = manager.deleteTaskById(created.getId());
        assertNotNull(removed);
        assertNull(manager.getTaskById(created.getId()));
    }

    @Test
    void deleteTaskById_shouldReturnNullForWrongId() {
        assertNull(manager.deleteTaskById(999));
    }

    @Test
    void deleteAllTasks_shouldClearList() {
        manager.createTask(new Task("T1", "D", Status.NEW));
        manager.createTask(new Task("T2", "D", Status.NEW));

        manager.deleteAllTasks();
        assertTrue(manager.getAllTasks().isEmpty());
    }

    // ---------- EPIC ----------
    @Test
    void createEpic_shouldCreateAndReturnWithId() {
        Epic epic = manager.createEpic(new Epic("E", "D"));
        assertNotNull(epic);
        assertTrue(epic.getId() > 0);

        Epic loaded = manager.getEpicById(epic.getId());
        assertNotNull(loaded);
        assertEquals(epic.getId(), loaded.getId());
        assertEquals(Status.NEW, loaded.getStatus(), "У нового эпика статус NEW (если нет подзадач)");
    }

    @Test
    void getAllEpics_shouldBeEmptyInitially() {
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    void getEpicById_shouldReturnNullForWrongId() {
        assertNull(manager.getEpicById(999));
    }

    @Test
    void updateEpic_shouldUpdateNameAndDescription() {
        Epic epic = manager.createEpic(new Epic("E", "D"));

        Epic updated = new Epic(epic.getId(), "E2", "D2");
        Epic result = manager.updateEpic(updated);

        assertNotNull(result);
        assertEquals("E2", manager.getEpicById(epic.getId()).getName());
        assertEquals("D2", manager.getEpicById(epic.getId()).getDescription());
    }

    @Test
    void updateEpic_shouldReturnNullForWrongId() {
        Epic updated = new Epic(999, "E2", "D2");
        assertNull(manager.updateEpic(updated));
    }

    @Test
    void deleteEpicById_shouldRemoveEpicAndItsSubtasks() {
        Epic epic = manager.createEpic(new Epic("E", "D"));
        Subtask s1 = manager.createSubtask(new Subtask("S1", "D", Status.NEW, epic.getId()));
        Subtask s2 = manager.createSubtask(new Subtask("S2", "D", Status.NEW, epic.getId()));

        assertNotNull(s1);
        assertNotNull(s2);

        manager.deleteEpicById(epic.getId());

        assertNull(manager.getEpicById(epic.getId()));
        assertNull(manager.getSubtaskById(s1.getId()));
        assertNull(manager.getSubtaskById(s2.getId()));
    }

    @Test
    void deleteEpicById_shouldReturnNullForWrongId() {
        assertNull(manager.deleteEpicById(999));
    }

    @Test
    void deleteAllEpics_shouldClearEpicsAndSubtasks() {
        Epic epic = manager.createEpic(new Epic("E", "D"));
        manager.createSubtask(new Subtask("S", "D", Status.NEW, epic.getId()));

        manager.deleteAllEpics();

        assertTrue(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty(), "При удалении всех эпиков подзадачи тоже очищаются");
    }

    // ---------- SUBTASK ----------
    @Test
    void createSubtask_shouldReturnNullIfEpicNotExists() {
        Subtask created = manager.createSubtask(new Subtask("S", "D", Status.NEW, 999));
        assertNull(created);
    }

    @Test
    void getAllSubtasks_shouldBeEmptyInitially() {
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    void getSubtaskById_shouldReturnNullForWrongId() {
        assertNull(manager.getSubtaskById(999));
    }

    @Test
    void updateSubtask_shouldUpdateExisting() {
        Epic epic = manager.createEpic(new Epic("E", "D"));
        Subtask created = manager.createSubtask(new Subtask("S", "D", Status.NEW, epic.getId()));

        Subtask updated = new Subtask(created.getId(), "S2", "D2", Status.DONE, epic.getId());
        Subtask result = manager.updateSubtask(updated);

        assertNotNull(result);
        assertEquals("S2", manager.getSubtaskById(created.getId()).getName());
        assertEquals(Status.DONE, manager.getSubtaskById(created.getId()).getStatus());
    }

    @Test
    void updateSubtask_shouldReturnNullForWrongId() {
        Epic epic = manager.createEpic(new Epic("E", "D"));
        Subtask updated = new Subtask(999, "S2", "D2", Status.DONE, epic.getId());
        assertNull(manager.updateSubtask(updated));
    }

    @Test
    void deleteSubtaskById_shouldRemoveAndClearIdFromEpic() {
        Epic epic = manager.createEpic(new Epic("E", "D"));
        Subtask created = manager.createSubtask(new Subtask("S", "D", Status.NEW, epic.getId()));

        manager.deleteSubtaskById(created.getId());

        assertNull(manager.getSubtaskById(created.getId()));
        assertFalse(manager.getEpicById(epic.getId()).getSubtaskIds().contains(created.getId()),
                "В эпике не должно остаться id удалённой подзадачи");
    }

    @Test
    void deleteSubtaskById_shouldReturnNullForWrongId() {
        assertNull(manager.deleteSubtaskById(999));
    }

    @Test
    void deleteAllSubtasks_shouldClearSubtasksAndEpicIds() {
        Epic epic = manager.createEpic(new Epic("E", "D"));
        manager.createSubtask(new Subtask("S1", "D", Status.NEW, epic.getId()));
        manager.createSubtask(new Subtask("S2", "D", Status.NEW, epic.getId()));

        manager.deleteAllSubtasks();

        assertTrue(manager.getAllSubtasks().isEmpty());
        assertTrue(manager.getEpicById(epic.getId()).getSubtaskIds().isEmpty(),
                "После deleteAllSubtasks в эпике не должно остаться id подзадач");
        assertEquals(Status.NEW, manager.getEpicById(epic.getId()).getStatus(),
                "После очистки подзадач статус эпика должен стать NEW");
    }

    // ---------- EXTRA ----------
    @Test
    void getSubtasksOfEpic_shouldReturnEmptyForWrongEpicId() {
        assertTrue(manager.getSubtasksOfEpic(999).isEmpty());
    }

    @Test
    void getSubtasksOfEpic_shouldReturnSubtasksForEpic() {
        Epic epic = manager.createEpic(new Epic("E", "D"));
        Subtask s1 = manager.createSubtask(new Subtask("S1", "D", Status.NEW, epic.getId()));
        Subtask s2 = manager.createSubtask(new Subtask("S2", "D", Status.NEW, epic.getId()));

        List<Subtask> subs = manager.getSubtasksOfEpic(epic.getId());
        assertEquals(2, subs.size());
        List<Integer> subIds = subs.stream()
                .map(Subtask::getId)
                .collect(Collectors.toList());

        assertTrue(subIds.containsAll(List.of(s1.getId(), s2.getId())));
    }

    // ---------- HISTORY via manager ----------
    @Test
    void history_shouldBeEmptyInitially() {
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void history_shouldRemoveDuplicates_keepLastView() {
        Task t1 = manager.createTask(new Task("T1", "D", Status.NEW));
        Task t2 = manager.createTask(new Task("T2", "D", Status.NEW));

        manager.getTaskById(t1.getId());
        manager.getTaskById(t2.getId());
        manager.getTaskById(t1.getId()); // t1 в конец без дубля

        List<Integer> ids = manager.getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList());

        assertEquals(List.of(t2.getId(), t1.getId()), ids);
    }
}