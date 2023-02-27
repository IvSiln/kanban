package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    protected Task creatTask() {
        return new Task("name", "detail", Status.NEW, LocalDateTime.now(), Duration.between(LocalTime.MIN, LocalTime.of(00, 30, 00)));
    }

    protected Epic creatEpic() {
        return new Epic("name", "detail", Status.NEW);
    }

    protected Subtask creatSubtask(Epic epic) {
        return new Subtask("name", "Title", Status.NEW, epic.getId(), LocalDateTime.now(), Duration.between(LocalTime.MIN, LocalTime.of(01, 30, 00)));
    }

    @Test
    void shouldAddTask() {
        Task task = creatTask();
        manager.addTask(task);
        var tasksList = manager.getTaskRepository();
        assertNotNull(task.getStatus());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals(Map.of(task.getId(), task), tasksList);
    }

    @Test
    void shouldUpdateTaskStatusInProgress() {
        Task task = creatTask();
        manager.addTask(task);
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);
        assertEquals(Status.IN_PROGRESS, manager.getTask(task.getId()).getStatus());
    }

    @Test
    void shouldUpdateTaskStatusDone() {
        Task task = creatTask();
        manager.addTask(task);
        task.setStatus(Status.DONE);
        manager.updateTask(task);
        assertEquals(Status.DONE, manager.getTask(task.getId()).getStatus());
    }

    @Test
    void shouldReturnNullWhenTaskNull() {
        Task task = manager.addTask(null);
        assertNull(task);
    }

    @Test
    public void shouldNotUpdateTaskIfNull() {
        Task task = creatTask();
        manager.addTask(task);
        manager.updateTask(null);
        assertEquals(task, manager.getTask(task.getId()));
    }

    @Test
    public void shouldDeleteAllTasks() {
        Task task = creatTask();
        manager.addTask(task);
        manager.clearAllTask();
        assertEquals(Collections.EMPTY_MAP, manager.getTaskRepository());
    }

    @Test
    public void shouldDeleteTaskById() {
        Task task = creatTask();
        manager.addTask(task);
        manager.clearTask(task.getId());
        assertEquals(Collections.EMPTY_MAP, manager.getTaskRepository());
    }

    @Test
    public void shouldDoNothingIfTaskHashMapIsEmpty() {
        manager.clearAllTask();
        manager.clearTask(999);
        assertEquals(0, manager.getTaskRepository().size());
    }

    @Test
    public void shouldReturnEmptyListTasksIfNoTasks() {
        assertTrue(manager.getTaskRepository().isEmpty());
    }

    @Test
    public void shouldReturnNullIfTaskDoesNotExist() {
        assertNull(manager.getTask(999));
    }

    @Test
    public void shouldAddEpic() {
        Epic epic = creatEpic();
        manager.addEpic(epic);
        Subtask subtask = creatSubtask(epic);
        manager.addSubtask(subtask);
        Map<Integer, Subtask> subtasksList = manager.getSubtaskRepository();
        assertNotNull(subtask.getStatus());
        assertEquals(epic.getId(), subtask.getEpicId());
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(Map.of(subtask.getId(), subtask), subtasksList);
    }

    @Test
    public void shouldUpdateEpicStatusInProgress() {
        Epic epic = creatEpic();
        manager.addEpic(epic);
        epic.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateEpicStatusDone() {
        Epic epic = creatEpic();
        manager.addEpic(epic);
        epic.setStatus(Status.DONE);
        assertEquals(Status.DONE, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void shouldReturnNullWhenEpicNull() {
        Epic epic = manager.addEpic(null);
        assertNull(epic);
    }

    @Test
    public void shouldNotUpdateEpicIfNull() {
        Epic epic = creatEpic();
        manager.addEpic(epic);
        manager.updateEpic(null);
        assertEquals(epic, manager.getEpic(epic.getId()));
    }

    @Test
    public void shouldDeleteAllEpics() {
        Epic epic = creatEpic();
        manager.addEpic(epic);
        manager.clearAllEpics();
        assertEquals(Collections.EMPTY_MAP, manager.getEpicRepository());
    }

    @Test
    public void shouldDeleteAllSubtasksByEpic() {
        Epic epic = creatEpic();
        manager.addEpic(epic);
        Subtask subtask = creatSubtask(epic);
        manager.addSubtask(subtask);
        manager.clearEpic(epic.getId());
        assertNull(manager.getEpic(epic.getId()));
        assertTrue(manager.getSubtaskRepository().isEmpty());
    }

    @Test
    public void shouldClearEpic() {
        Epic epic = creatEpic();
        manager.addEpic(epic);
        manager.clearEpic(epic.getId());
        assertEquals(Collections.EMPTY_MAP, manager.getEpicRepository());
    }

    @Test
    public void shouldNotDeleteEpicIfBadId() {
        Epic epic = creatEpic();
        manager.addEpic(epic);
        manager.clearEpic(999);
        assertEquals(Map.of(epic.getId(), epic), manager.getEpicRepository());
    }

    @Test
    public void shouldDoNothingIfEpicHashMapIsEmpty() {
        manager.clearAllEpics();
        manager.clearEpic(999);
        assertTrue(manager.getEpicRepository().isEmpty());
    }

    @Test
    public void shouldReturnEmptyListEpicsIfNoEpics() {
        assertTrue(manager.getEpicRepository().isEmpty());
    }

    @Test
    public void shouldAddSubtask() {
        Epic epic = creatEpic();
        manager.addEpic(epic);
        Subtask subtask = creatSubtask(epic);
        manager.addSubtask(subtask);
        Map<Integer, Subtask> subtasksMap = manager.getSubtaskRepository();
        assertNotNull(subtask.getStatus());
        assertEquals(epic.getId(), subtask.getEpicId());
        assertEquals(Status.NEW, subtask.getStatus());
        assertEquals(subtask, subtasksMap.get(subtask.getId()));
        assertEquals(epic.getSubtasksList(), List.of(subtask));
    }

    @Test
    public void shouldUpdateSubtaskStatusInProgress() {
        Epic epic = creatEpic();
        manager.addEpic(epic);
        Subtask subtask = creatSubtask(epic);
        manager.addSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask);
        assertEquals(Status.IN_PROGRESS, manager.getSubtask(subtask.getId()).getStatus());
        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateSubtaskStatusDone() {
        Epic epic = creatEpic();
        manager.addEpic(epic);
        Subtask subtask = creatSubtask(epic);
        manager.addSubtask(subtask);
        subtask.setStatus(Status.DONE);
        manager.updateSubtask(subtask);
        assertEquals(Status.DONE, manager.getSubtask(subtask.getId()).getStatus());
        assertEquals(Status.DONE, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void shouldReturnNullWhenSubtaskNull() {
        Subtask subtask = manager.addSubtask(null);
        assertNull(subtask);
    }

    @Test
    public void shouldNotUpdateSubtaskIfNull() {
        Epic epic = creatEpic();
        manager.addEpic(epic);
        Subtask subtask = creatSubtask(epic);
        manager.addSubtask(subtask);
        manager.updateSubtask(null);
        assertEquals(subtask, manager.getSubtask(subtask.getId()));
    }

    @Test
    public void shouldDeleteAllSubtasks() {
        Epic epic = creatEpic();
        manager.addEpic(epic);
        Subtask subtask = creatSubtask(epic);
        manager.addSubtask(subtask);
        manager.clearAllSubtask();
        assertTrue(epic.getSubtasksList().isEmpty());
        assertTrue(manager.getSubtaskRepository().isEmpty());
    }

    @Test
    void shouldNotDeleteSubtaskIfBadId() {
        Epic epic = creatEpic();
        manager.addEpic(epic);
        Subtask subtask = creatSubtask(epic);
        manager.addSubtask(subtask);
        manager.clearSubtask(1000);
        assertEquals(Map.of(subtask.getId(), subtask), manager.getSubtaskRepository());
        assertEquals(subtask.getEpicId(), manager.getEpic(epic.getId()).getId());
    }

    @Test
    void shouldDoNothingIfSubtaskHashMapIsEmpty() {
        manager.clearAllEpics();
        manager.clearSubtask(1000);
        assertEquals(0, manager.getSubtaskRepository().size());
    }

    @Test
    void shouldReturnEmptyListSubtasksIfNoSubtasks() {
        assertTrue(manager.getSubtaskRepository().isEmpty());
    }

    @Test
    void shouldReturnNullIfSubtaskDoesNotExist() {
        assertNull(manager.getSubtask(1000));
    }

    @Test
    void shouldReturnEmptyHistory() {
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    void shouldReturnEmptyHistoryIfTasksNotExist() {
        manager.getTask(1000);
        manager.getSubtask(1000);
        manager.getEpic(1000);
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void shouldReturnHistoryWithTasks() {
        Epic epic = creatEpic();
        manager.addEpic(epic);
        Subtask subtask = creatSubtask(epic);
        manager.addSubtask(subtask);
        manager.getEpic(epic.getId());
        manager.getSubtask(subtask.getId());
        List<Task> list = manager.getHistory();
        assertEquals(2, list.size());
        assertTrue(list.contains(subtask));
        assertTrue(list.contains(epic));
    }
}