package manager;

import enums.Status;
import interfaces.HistoryManager;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void history_shouldBeEmptyInitially() {
        HistoryManager history = new InMemoryHistoryManager();
        assertTrue(history.getHistory().isEmpty());
    }

    @Test
    void add_shouldAddTask() {
        HistoryManager history = new InMemoryHistoryManager();
        history.add(new Task(1, "T1", "D1", Status.NEW));

        List<Task> h = history.getHistory();
        assertEquals(1, h.size());
        assertEquals(1, h.get(0).getId());
    }

    @Test
    void add_shouldRemoveDuplicates_keepLastViewOnly() {
        HistoryManager history = new InMemoryHistoryManager();

        Task t1 = new Task(1, "T1", "D1", Status.NEW);
        Task t2 = new Task(2, "T2", "D2", Status.NEW);

        history.add(t1); // [1]
        history.add(t2); // [1,2]
        history.add(t1); // [2,1]

        List<Integer> ids = history.getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList());

        assertEquals(List.of(2, 1), ids);
    }

    @Test
    void remove_shouldRemoveFromBeginningMiddleEnd() {
        HistoryManager history = new InMemoryHistoryManager();

        history.add(new Task(1, "T1", "D1", Status.NEW));
        history.add(new Task(2, "T2", "D2", Status.NEW));
        history.add(new Task(3, "T3", "D3", Status.NEW));
        // [1,2,3]

        history.remove(1); // remove beginning -> [2,3]
        assertEquals(List.of(2, 3), history.getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList()));

        history.remove(3); // remove end -> [2]
        assertEquals(List.of(2), history.getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList()));

        history.add(new Task(1, "T1", "D1", Status.NEW)); // [2,1]
        history.add(new Task(3, "T3", "D3", Status.NEW)); // [2,1,3]
        history.remove(1); // remove middle -> [2,3]
        assertEquals(List.of(2, 3), history.getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList()));
    }

    @Test
    void remove_nonExistingId_shouldNotThrow() {
        HistoryManager history = new InMemoryHistoryManager();
        history.remove(999);
        assertTrue(history.getHistory().isEmpty());
    }
}