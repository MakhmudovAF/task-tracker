import interfaces.TaskManager;
import manager.Managers;
import model.Epic;
import model.Subtask;
import model.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        System.out.println("=== Создание задач ===");

        // Создаем эпик
        Epic epic = new Epic("Переезд", "Организация переезда в новый офис");
        manager.createEpic(epic);
        int epicId = epic.getId();
        System.out.println("Создан эпик с ID: " + epicId);

        // Создаем подзадачи
        Subtask subtask1 = new Subtask("Собрать коробки", "Купить и собрать коробки для переезда",
                "NEW", epicId);
        Subtask subtask2 = new Subtask("Упаковать компьютеры", "Аккуратно упаковать всю технику",
                "NEW", epicId);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        System.out.println("Созданы подзадачи с ID: " + subtask1.getId() + " и " + subtask2.getId());

        // Создаем обычную задачу
        Task task = new Task("Заказать пиццу", "Для команды после переезда", "NEW");
        manager.createTask(task);
        System.out.println("Создана задача с ID: " + task.getId());

        System.out.println("\n=== Просмотр задач (добавление в историю) ===");

        // Просматриваем задачи несколько раз
        System.out.println("1. Просмотр задачи с ID " + task.getId() + ": " + manager.getTaskById(task.getId()));
        System.out.println("2. Просмотр эпика с ID " + epicId + ": " + manager.getEpicById(epicId));
        System.out.println("3. Просмотр подзадачи с ID " + subtask1.getId() + ": " + manager.getSubtaskById(subtask1.getId()));

        // Еще раз просматриваем ту же задачу (добавится дубль)
        System.out.println("4. Снова просмотр задачи с ID " + task.getId() + ": " + manager.getTaskById(task.getId()));
        System.out.println("5. Просмотр подзадачи с ID " + subtask2.getId() + ": " + manager.getSubtaskById(subtask2.getId()));

        System.out.println("\n=== История просмотров (первые " + Math.min(manager.getHistory().size(), 10) + " записей) ===");
        for (Task t : manager.getHistory()) {
            System.out.println(t);
        }

        System.out.println("\n=== Изменение статусов ===");

        subtask1.setStatus("IN_PROGRESS");
        manager.updateSubtask(subtask1);
        System.out.println("Статус подзадачи " + subtask1.getId() + " изменен на IN_PROGRESS");
        System.out.println("Статус эпика после изменения: " + manager.getEpicById(epicId).getStatus());

        System.out.println("\n=== Еще раз просмотрим эпик (добавится в историю) ===");
        System.out.println("Эпик: " + manager.getEpicById(epicId));

        System.out.println("\n=== Обновленная история ===");
        for (Task t : manager.getHistory()) {
            System.out.println(t);
        }

        System.out.println("\n=== Завершение всех подзадач ===");

        subtask1.setStatus("DONE");
        subtask2.setStatus("DONE");
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);
        System.out.println("Все подзадачи завершены");
        System.out.println("Статус эпика: " + manager.getEpicById(epicId).getStatus());

        System.out.println("\n=== Просмотр эпика после завершения ===");
        System.out.println("Эпик: " + manager.getEpicById(epicId));
        System.out.println("Эпик: " + manager.getEpicById(epicId));
        System.out.println("Эпик: " + manager.getEpicById(epicId));

        System.out.println("\n=== Финальная история просмотров ===");
        System.out.println("Всего записей в истории: " + manager.getHistory().size());
        for (int i = 0; i < manager.getHistory().size(); i++) {
            System.out.println((i + 1) + ". " + manager.getHistory().get(i));
        }

        System.out.println("\n=== Все задачи в системе ===");
        System.out.println("Обычные задачи: " + manager.getTasks().size());
        System.out.println("Эпики: " + manager.getEpics().size());
        System.out.println("Подзадачи: " + manager.getSubtasks().size());

        System.out.println("\n=== Все задачи (сгруппированные) ===");
        printAllTasks(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Обычные задачи:");
        for (Task task : manager.getTasks().values()) {
            System.out.println("  " + task);
        }

        System.out.println("\nЭпики:");
        for (model.Epic epic : manager.getEpics().values()) {
            System.out.println("  " + epic);

            System.out.println("  Подзадачи эпика " + epic.getId() + ":");
            for (model.Subtask subtask : manager.getSubtasksByEpicId(epic.getId())) {
                System.out.println("    -> " + subtask);
            }
        }

        System.out.println("\nИстория просмотров (" + manager.getHistory().size() + "):");
        for (Task task : manager.getHistory()) {
            System.out.println("  " + task);
        }
    }
}