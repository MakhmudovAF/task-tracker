import enums.Status;
import manager.Managers;
import manager.TaskManager;
import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdConflictTest {
    @Test
    void manualIdShouldNotConflictWithGeneratedId() {
        TaskManager manager = Managers.getDefault();

        Task taskWithManualId = new Task(999, "T", "D", Status.NEW);
        Task created = manager.createTask(taskWithManualId);

        assertNotNull(created);
        assertNotEquals(999, created.getId(), "Менеджер должен назначить новый id, а не сохранять вручную заданный");
        assertNotNull(manager.getTaskById(created.getId()), "Задача должна находиться по назначенному id");
        assertNull(manager.getTaskById(999), "По вручную заданному id задача находиться не должна (если менеджер его заменил)");
    }
}