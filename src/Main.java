import enums.Status;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task t1 = manager.createTask(new Task("Переезд", "Собрать вещи", Status.NEW));
        Epic e1 = manager.createEpic(new Epic("Праздник", "Сделать всё"));
        Subtask s1 = manager.createSubtask(new Subtask("Торт", "Заказать торт", Status.NEW, e1.getId()));

        System.out.println(manager.getTaskById(t1.getId()));
        System.out.println(manager.getEpicById(e1.getId()));
        System.out.println(manager.getSubtaskById(s1.getId()));

        System.out.println("History:");
        System.out.println(manager.getHistory());
    }
}