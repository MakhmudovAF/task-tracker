package manager;

import enums.Status;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    @Test
    void shouldNotCreateSubtaskIfEpicDoesNotExist() {
        TaskManager manager = Managers.getDefault();

        Subtask subtask = new Subtask("S", "D", Status.NEW, 999999); // несуществующий epicId
        Subtask created = manager.createSubtask(subtask);

        assertNull(created, "Подзадачу нельзя создавать без существующего эпика");
        assertTrue(manager.getAllSubtasks().isEmpty(), "В менеджере не должно появиться подзадач");
    }

    @Test
    void shouldNotAllowChangingSubtaskEpicId() {
        TaskManager manager = Managers.getDefault();

        Epic epic = manager.createEpic(new Epic("E", "D"));
        Subtask s = manager.createSubtask(new Subtask("S", "SD", Status.NEW, epic.getId()));
        assertNotNull(s);

        // пытаемся "сделать сабтаск своим эпиком" — меняем epicId на id самой сабтаски
        Subtask updated = new Subtask(s.getId(), s.getName(), s.getDescription(), s.getStatus(), s.getId());
        Subtask result = manager.updateSubtask(updated);

        assertNull(result, "Нельзя менять epicId подзадачи (в т.ч. делать её своим же эпиком)");
    }

    @Test
    void shouldCreateAndFindTaskEpicSubtaskById() {
        TaskManager manager = Managers.getDefault();

        Task t = manager.createTask(new Task("T", "TD", Status.NEW));
        Epic e = manager.createEpic(new Epic("E", "ED"));
        Subtask s = manager.createSubtask(new Subtask("S", "SD", Status.IN_PROGRESS, e.getId()));

        assertNotNull(manager.getTaskById(t.getId()));
        assertNotNull(manager.getEpicById(e.getId()));
        assertNotNull(manager.getSubtaskById(s.getId()));
    }
}