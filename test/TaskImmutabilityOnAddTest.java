import enums.Status;
import manager.Managers;
import manager.TaskManager;
import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskImmutabilityOnAddTest {
    @Test
    void taskFieldsShouldNotChangeWhenAddedToManagerExceptId() {
        TaskManager manager = Managers.getDefault();

        Task task = new Task("Name", "Desc", Status.IN_PROGRESS);
        Task created = manager.createTask(task);

        // id менеджер назначает — это ожидаемое изменение
        assertTrue(created.getId() > 0);

        // остальные поля должны остаться ровно такими же
        assertEquals("Name", created.getName());
        assertEquals("Desc", created.getDescription());
        assertEquals(Status.IN_PROGRESS, created.getStatus());

        Task saved = manager.getTaskById(created.getId());
        assertEquals(created, saved, "Сохранённая задача должна совпадать по id");
        assertEquals(created.getName(), saved.getName());
        assertEquals(created.getDescription(), saved.getDescription());
        assertEquals(created.getStatus(), saved.getStatus());
    }
}