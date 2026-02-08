package model;

import enums.Status;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void getEndTime_shouldReturnNull_whenStartTimeIsNull() {
        Task task = new Task("T", "D", Status.NEW);
        task.setDuration(30);
        task.setStartTime(null);

        assertNull(task.getEndTime(), "Если startTime == null, то endTime должен быть null");
    }

    @Test
    void getEndTime_shouldCalculateFromStartTimeAndDuration() {
        Task task = new Task("T", "D", Status.NEW);
        task.setDuration(90);

        LocalDateTime start = LocalDateTime.of(2025, 1, 10, 12, 0);
        task.setStartTime(start);

        assertEquals(start.plusMinutes(90), task.getEndTime());
    }
}