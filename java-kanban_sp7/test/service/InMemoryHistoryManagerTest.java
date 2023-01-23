package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    private int id = 0;

    public int generateId() {
        return ++id;
    }

    protected Task addTask() {
        return new Task("Name", "Detail", Status.NEW);
    }

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldAddTaskToHistory() {
        Task task = generateTask();
        Task task1 = generateTask();
        Task task2 = generateTask();
        historyManager.add(task);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task.getId());
        assertEquals(List.of(task1, task2), historyManager.getHistory());
    }

    public Task generateTask() {
        Task task = addTask();
        int taskId = generateId();
        task.setId(taskId);
        return task;
    }

    @Test
    void shouldRemoveOneTask() {
        Task task = generateTask();
        historyManager.add(task);
        historyManager.remove(task.getId());
        assertEquals(Collections.EMPTY_LIST, historyManager.getHistory());
    }

    @Test
    void shouldCantRemoveTaskWithWrongId() {
        Task task = generateTask();
        historyManager.add(task);
        historyManager.remove(99);
        assertEquals(List.of(task), historyManager.getHistory());
    }
}