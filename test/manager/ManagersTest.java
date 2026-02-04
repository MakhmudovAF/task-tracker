package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void getDefaultShouldReturnInitializedTaskManager() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "Managers.getDefault() должен возвращать менеджер");
        assertNotNull(manager.getAllTasks(), "Менеджер должен быть готов к работе");
        assertNotNull(manager.getHistory(), "История должна быть доступна");
    }

    @Test
    void getDefaultHistoryShouldReturnInitializedHistoryManager() {
        HistoryManager history = Managers.getDefaultHistory();
        assertNotNull(history, "Managers.getDefaultHistory() должен возвращать HistoryManager");
        assertNotNull(history.getHistory(), "HistoryManager должен быть готов к работе");
    }
}