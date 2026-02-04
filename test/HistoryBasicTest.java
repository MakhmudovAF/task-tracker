import enums.Status;
import manager.Managers;
import manager.TaskManager;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryBasicTest {
    @Test
    void historyShouldAddOnGetAndKeepOnlyLast10() {
        TaskManager manager = Managers.getDefault();

        // создаём 11 задач и просматриваем
        int firstId = -1;
        for (int i = 0; i < 11; i++) {
            Task t = manager.createTask(new Task("T" + i, "D" + i, Status.NEW));
            if (i == 0) firstId = t.getId();
            manager.getTaskById(t.getId());
        }

        List<Task> history = manager.getHistory();
        assertEquals(10, history.size(), "История должна хранить максимум 10 просмотров");

        // самый первый просмотр должен вытесниться
        int finalFirstId = firstId;
        boolean containsFirst = history.stream().anyMatch(t -> t.getId() == finalFirstId);
        assertFalse(containsFirst, "Самый старый просмотр должен удаляться при переполнении");
    }
}