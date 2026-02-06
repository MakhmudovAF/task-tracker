import enums.Status;
import manager.Managers;
import interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task = manager.createTask(new Task("Переезд", "Собрать вещи", Status.NEW));
        Epic epic = manager.createEpic(new Epic("Праздник", "Сделать всё"));
        Subtask subtask = manager.createSubtask(
                new Subtask("Торт", "Заказать торт", Status.NEW, epic.getId())
        );

        for (int i = 0; i < 4; i++) {
            manager.getTaskById(task.getId());
            manager.getEpicById(epic.getId());
            manager.getSubtaskById(subtask.getId());

            printHistory(manager);
        }
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("History:");
        System.out.println(manager.getHistory());
        System.out.println();
    }
}