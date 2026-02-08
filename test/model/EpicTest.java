package model;

import enums.Status;
import interfaces.TaskManager;
import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

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
}