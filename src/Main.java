import enums.Status;
import manager.Managers;
import interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task = manager.createTask(new Task("Переезд", "Собрать вещи", Status.NEW));
        Epic epic = manager.createEpic(new Epic("Праздник", "Сделать всё"));
        Subtask subtask1 = manager.createSubtask(
                new Subtask("Торт1", "Заказать торт1", Status.NEW, epic.getId(), 15, LocalDateTime.of(2026, 2, 8, 19, 21))
        );
        Subtask subtask2 = manager.createSubtask(
                new Subtask("Торт2", "Заказать торт2", Status.NEW, epic.getId(), 30, LocalDateTime.of(2026, 2, 9, 19, 21))
        );

        for (int i = 0; i < 4; i++) {
            manager.getTaskById(task.getId());
            manager.getEpicById(epic.getId());
            manager.getSubtaskById(subtask1.getId());
            manager.getSubtaskById(subtask2.getId());

            printHistory(manager);
        }
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("History:");
        System.out.println(manager.getHistory());
        System.out.println();
    }
}