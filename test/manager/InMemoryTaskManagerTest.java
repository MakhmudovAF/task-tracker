package manager;

import interfaces.TaskManager;
import interfaces.TaskManagerTest;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {
    @Override
    protected TaskManager createManager() {
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }
}