package manager;

import interfaces.HistoryManager;
import model.Node;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList history = new CustomLinkedList();
    private final Map<Integer, Node> nodeById = new HashMap<>();

    private class CustomLinkedList {
        private Node head;
        private Node tail;

        void linkLast(Task task) {
            Node oldTail = tail;
            Node newTail = new Node(oldTail, task, null);

            tail = newTail;
            if (oldTail == null) {
                head = newTail;
            } else {
                oldTail.setNext(newTail);
            }

            nodeById.put(task.getId(), newTail);
        }

        void removeNode(Node node) {
            Node prev = node.getPrevious();
            Node next = node.getNext();

            if (prev != null) {
                prev.setNext(next);
            } else {
                head = next;
            }

            if (next != null) {
                next.setPrevious(prev);
            } else {
                tail = prev;
            }
        }
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        history.linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = nodeById.remove(id);
        if (node != null) {
            history.removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        Node current = history.head;

        while (current != null) {
            result.add(current.getData());
            current = current.getNext();
        }
        return result;
    }
}