package model;

public class Node {
    private final Task data;
    private Node next;
    private Node previous;

    public Node(Node previous, Task data, Node next) {
        this.previous = previous;
        this.data = data;
        this.next = next;
    }

    public Task getData() {
        return data;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrevious() {
        return previous;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }
}