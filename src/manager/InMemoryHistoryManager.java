package manager;

import interfaces.HistoryManager;
import model.Node;
import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList history = new CustomLinkedList();
    private final Map<Integer, Node> map = new HashMap<>();

    class CustomLinkedList {
        private Node head;
        private Node tail;
        private int size = 0;

        public void addLast(Task task) {
            final Node oldTail = tail;
            final Node newTail = new Node(oldTail, task, null);

            tail = newTail;
            if (oldTail == null) {
                head = newTail;
            } else {
                oldTail.setNext(newTail);
            }
            size++;

            map.put(task.getId(), newTail);
        }

        public void removeNode(Node node) {
            final Node prev = node.getPrevious();
            final Node next = node.getNext();

            if (prev != null) {
                prev.setNext(next);
            } else {
                history.setHead(next);
            }

            if (next != null) {
                next.setPrevious(prev);
            } else {
                history.setTail(prev);
            }
            size--;
        }

        public int size() {
            return this.size;
        }

        public Node getHead() {
            return head;
        }

        public void setHead(Node head) {
            this.head = head;
        }

        public void setTail(Node tail) {
            this.tail = tail;
        }
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        remove(task.getId());
        history.addLast(task);
    }

    @Override
    public void remove(int id) {
        Node removed = map.remove(id);

        if (removed != null) {
            history.removeNode(removed);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> hist = new ArrayList<>();
        Node current = history.getHead();

        while (current != null) {
            hist.add(current.getData());
            current = current.getNext();
        }
        return hist;
    }
}
