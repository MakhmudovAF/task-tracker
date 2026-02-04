package model;

import enums.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void tasksShouldBeEqualIfIdsAreEqual() {
        Task t1 = new Task(1, "A", "B", Status.NEW);
        Task t2 = new Task(1, "X", "Y", Status.DONE);

        assertEquals(t1, t2, "Task с одинаковым id должны быть равны");
        assertEquals(t1.hashCode(), t2.hashCode(), "У равных Task должны совпадать hashCode");
    }
}