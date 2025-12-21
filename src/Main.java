import model.Epic;
import model.Subtask;
import model.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Epic epic = new Epic("Переезд", "Организация переезда в новый офис");
        manager.createEpic(epic);
        int epicId = epic.getId();

        Subtask subtask1 = new Subtask("Собрать коробки", "Купить и собрать коробки для переезда",
                "NEW", epicId);
        Subtask subtask2 = new Subtask("Упаковать компьютеры", "Аккуратно упаковать всю технику",
                "NEW", epicId);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Task task = new Task("Заказать пиццу", "Для команды после переезда",
                "NEW");
        manager.createTask(task);

        System.out.println("Все задачи:");
        for (Task t : manager.getTasks().values()) {
            System.out.println(t);
        }

        System.out.println("\nВсе эпики:");
        for (Epic e : manager.getEpics().values()) {
            System.out.println(e);
        }

        System.out.println("\nВсе подзадачи:");
        for (Subtask st : manager.getSubtasks().values()) {
            System.out.println(st);
        }

        subtask1.setStatus("IN_PROGRESS");
        manager.updateSubtask(subtask1);

        System.out.println("\nПосле изменения статуса подзадачи:");
        System.out.println("Статус эпика: " + manager.getEpicById(epicId).getStatus());

        subtask1.setStatus("DONE");
        subtask2.setStatus("DONE");
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);

        System.out.println("\nПосле завершения всех подзадач:");
        System.out.println("Статус эпика: " + manager.getEpicById(epicId).getStatus());
    }
}