import enums.Status;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = manager.createTask(new Task(
                "Переезд",
                "Собрать вещи и организовать перевозку",
                Status.NEW
        ));

        Task task2 = manager.createTask(new Task(
                "Покупка техники",
                "Выбрать и купить холодильник",
                Status.IN_PROGRESS
        ));

        Epic epic1 = manager.createEpic(new Epic(
                "Организовать семейный праздник",
                "Подготовить праздник целиком"
        ));

        Subtask epic1Sub1 = manager.createSubtask(new Subtask(
                "Составить список гостей",
                "Определить, кого приглашаем",
                Status.NEW,
                epic1.getId()
        ));

        Subtask epic1Sub2 = manager.createSubtask(new Subtask(
                "Заказать торт",
                "Выбрать кондитерскую и оформить заказ",
                Status.NEW,
                epic1.getId()
        ));

        Epic epic2 = manager.createEpic(new Epic(
                "Купить квартиру",
                "Найти и купить подходящий вариант"
        ));

        Subtask epic2Sub1 = manager.createSubtask(new Subtask(
                "Подобрать варианты",
                "Отобрать 10 объявлений по параметрам",
                Status.IN_PROGRESS,
                epic2.getId()
        ));

        System.out.println("=== TASKS ===");
        System.out.println(manager.getAllTasks());

        System.out.println("\n=== EPICS ===");
        System.out.println(manager.getAllEpics());

        System.out.println("\n=== SUBTASKS ===");
        System.out.println(manager.getAllSubtasks());

        System.out.println("\n=== SUBTASKS OF EPIC 1 ===");
        List<Subtask> epic1Subs = manager.getSubtasksOfEpic(epic1.getId());
        System.out.println(epic1Subs);

        System.out.println("\n=== SUBTASKS OF EPIC 2 ===");
        List<Subtask> epic2Subs = manager.getSubtasksOfEpic(epic2.getId());
        System.out.println(epic2Subs);

        Task updatedTask1 = new Task(
                task1.getId(),
                task1.getName(),
                task1.getDescription(),
                Status.DONE
        );
        manager.updateTask(updatedTask1);

        Subtask updatedEpic1Sub1 = new Subtask(
                epic1Sub1.getId(),
                epic1Sub1.getName(),
                epic1Sub1.getDescription(),
                Status.IN_PROGRESS,
                epic1.getId()
        );
        manager.updateSubtask(updatedEpic1Sub1);

        Subtask updatedEpic1Sub2 = new Subtask(
                epic1Sub2.getId(),
                epic1Sub2.getName(),
                epic1Sub2.getDescription(),
                Status.DONE,
                epic1.getId()
        );
        manager.updateSubtask(updatedEpic1Sub2);

        Subtask updatedEpic2Sub1 = new Subtask(
                epic2Sub1.getId(),
                epic2Sub1.getName(),
                epic2Sub1.getDescription(),
                Status.DONE,
                epic2.getId()
        );
        manager.updateSubtask(updatedEpic2Sub1);

        System.out.println("\n=== AFTER STATUS CHANGES ===");
        System.out.println("TASKS:");
        System.out.println(manager.getAllTasks());

        System.out.println("\nEPICS (status is calculated):");
        System.out.println(manager.getAllEpics());

        System.out.println("\nSUBTASKS:");
        System.out.println(manager.getAllSubtasks());

        manager.deleteTaskById(task2.getId());
        manager.deleteEpicById(epic1.getId());

        System.out.println("\n=== AFTER DELETIONS (task2 + epic1) ===");
        System.out.println("TASKS:");
        System.out.println(manager.getAllTasks());

        System.out.println("\nEPICS:");
        System.out.println(manager.getAllEpics());

        System.out.println("\nSUBTASKS:");
        System.out.println(manager.getAllSubtasks());
    }
}