import interfaces.TaskManager;
import manager.Managers;
import model.Epic;
import model.Subtask;
import model.Task;
import enums.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = manager.createTask(
                new Task("Задача 1", "Описание", TaskStatus.NEW)
        );

        Epic epic = manager.createEpic(
                new Epic("Эпик", "Большая задача")
        );

        Subtask sub1 = manager.createSubtask(
                new Subtask("Подзадача 1", "Описание",
                        TaskStatus.IN_PROGRESS, epic.getId())
        );

        manager.getTask(task1.getId());
        manager.getEpic(epic.getId());
        manager.getSubtask(sub1.getId());
        manager.getTask(task1.getId());

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}