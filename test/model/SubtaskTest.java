package model;

import enums.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    void subtasksShouldBeEqualIfIdsAreEqual() {
        Subtask s1 = new Subtask(7, "S1", "D1", Status.NEW, 100);
        Subtask s2 = new Subtask(7, "S2", "D2", Status.DONE, 200);

        assertEquals(s1, s2, "Subtask с одинаковым id должны быть равны");
        assertEquals(s1.hashCode(), s2.hashCode(), "У равных Subtask должны совпадать hashCode");
    }
}