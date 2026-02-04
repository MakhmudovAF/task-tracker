package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void epicsShouldBeEqualIfIdsAreEqual() {
        Epic e1 = new Epic(10, "E1", "D1");
        Epic e2 = new Epic(10, "E2", "D2");

        assertEquals(e1, e2, "Epic с одинаковым id должны быть равны");
        assertEquals(e1.hashCode(), e2.hashCode(), "У равных Epic должны совпадать hashCode");
    }
}