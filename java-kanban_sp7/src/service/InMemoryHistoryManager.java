package service;

import model.Node;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> historyRepository = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public void add(Task task) {
        if ((task != null)) {
            remove(task.getId());
            addLast(task);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(historyRepository.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void addLast(Task element) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, element, null);
        tail = newNode;
        historyRepository.put(element.getId(), newNode);
        if (oldTail == null)
            head = newNode;
        else
            oldTail.next = newNode;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            tasks.add(node.getData());
            node = node.next;
        }
        return tasks;
    }

    private void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        }
        Node<Task> next = node.next;
        Node<Task> prev = node.prev;
        node.setData(null);

        if (head == tail && tail == node) {
            head = null;
            tail = null;
        } else if (head == node) {
            head = next;
            head.prev = null;
        } else if (tail == node) {
            tail = prev;
            tail.next = null;
        } else {
            prev.next = next;
            next.prev = prev;
        }
    }
}
