package model;

import enums.Status;
import interfaces.TaskManager;
import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private TaskManager createManager() {
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Test
    void epicStatus_shouldBeNew_whenNoSubtasks() {
        TaskManager taskManager = createManager();

        Epic epic = taskManager.createEpic(new Epic("E", "D"));
        Epic saved = taskManager.getEpicById(epic.getId());

        assertNotNull(saved);
        assertEquals(Status.NEW, saved.getStatus(), "Если у эпика нет подзадач — статус NEW");
    }

    @Test
    void epicStatus_shouldBeNew_whenAllSubtasksNew() {
        TaskManager manager = createManager();

        Epic epic = manager.createEpic(new Epic("E", "D"));
        manager.createSubtask(new Subtask("S1", "D", Status.NEW, epic.getId()));
        manager.createSubtask(new Subtask("S2", "D", Status.NEW, epic.getId()));

        assertEquals(Status.NEW, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void epicStatus_shouldBeDone_whenAllSubtasksDone() {
        TaskManager manager = createManager();

        Epic epic = manager.createEpic(new Epic("E", "D"));
        Subtask s1 = manager.createSubtask(new Subtask("S1", "D", Status.DONE, epic.getId()));
        Subtask s2 = manager.createSubtask(new Subtask("S2", "D", Status.DONE, epic.getId()));

        assertNotNull(s1);
        assertNotNull(s2);
        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void epicStatus_shouldBeInProgress_whenSubtasksNewAndDone() {
        TaskManager manager = createManager();

        Epic epic = manager.createEpic(new Epic("E", "D"));
        manager.createSubtask(new Subtask("S1", "D", Status.NEW, epic.getId()));
        manager.createSubtask(new Subtask("S2", "D", Status.DONE, epic.getId()));

        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void epicStatus_shouldBeInProgress_whenAnySubtaskInProgress() {
        TaskManager manager = createManager();

        Epic epic = manager.createEpic(new Epic("E", "D"));
        manager.createSubtask(new Subtask("S1", "D", Status.IN_PROGRESS, epic.getId()));

        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void epicTimeFields_shouldBeZeroAndNull_whenNoSubtasks() {
        TaskManager manager = createManager();
        Epic epic = manager.createEpic(new Epic("E", "D"));

        Epic loaded = manager.getEpicById(epic.getId());
        assertNotNull(loaded);

        assertEquals(0, loaded.getDuration(), "Длительность эпика без подзадач должна быть 0");
        assertNull(loaded.getStartTime(), "startTime эпика без подзадач должен быть null");
        assertNull(loaded.getEndTime(), "endTime эпика без подзадач должен быть null");
    }

    @Test
    void epicTimeFields_shouldBeCalculatedFromSubtasks() {
        TaskManager manager = createManager();
        Epic epic = manager.createEpic(new Epic("E", "D"));

        // Подзадача 1: 10:00 + 30 мин => 10:30
        Subtask s1 = new Subtask("S1", "D", Status.NEW, epic.getId());
        s1.setStartTime(LocalDateTime.of(2025, 1, 10, 10, 0));
        s1.setDuration(30);
        s1 = manager.createSubtask(s1);

        // Подзадача 2: 09:00 + 60 мин => 10:00 (самая ранняя startTime)
        Subtask s2 = new Subtask("S2", "D", Status.NEW, epic.getId());
        s2.setStartTime(LocalDateTime.of(2025, 1, 10, 9, 0));
        s2.setDuration(60);
        s2 = manager.createSubtask(s2);

        // Подзадача 3: 11:00 + 15 мин => 11:15 (самая поздняя endTime)
        Subtask s3 = new Subtask("S3", "D", Status.NEW, epic.getId());
        s3.setStartTime(LocalDateTime.of(2025, 1, 10, 11, 0));
        s3.setDuration(15);
        s3 = manager.createSubtask(s3);

        Epic loaded = manager.getEpicById(epic.getId());

        assertEquals(30 + 60 + 15, loaded.getDuration(), "Длительность эпика = сумма длительностей подзадач");
        assertEquals(LocalDateTime.of(2025, 1, 10, 9, 0), loaded.getStartTime(), "startTime эпика = самый ранний startTime");
        assertEquals(LocalDateTime.of(2025, 1, 10, 11, 15), loaded.getEndTime(), "endTime эпика = самый поздний endTime");
    }

    @Test
    void epicTimeFields_shouldIgnoreNullStartTimes() {
        TaskManager manager = createManager();
        Epic epic = manager.createEpic(new Epic("E", "D"));

        Subtask s1 = new Subtask("S1", "D", Status.NEW, epic.getId());
        s1.setDuration(30);
        s1.setStartTime(null);
        manager.createSubtask(s1);

        Epic loaded = manager.getEpicById(epic.getId());

        assertEquals(30, loaded.getDuration());
        assertNull(loaded.getStartTime(), "Если у всех подзадач startTime == null, startTime эпика должен быть null");
        assertNull(loaded.getEndTime(), "Если у всех подзадач startTime == null, endTime эпика должен быть null");
    }
}