package http;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.TaskManager;
import service.TaskManagerTest;
import util.Managers;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class HttpTaskManagerTest <T extends TaskManagerTest<HttpTaskManager>> {
    private KVServer server;
    private TaskManager manager;

    @BeforeEach
    public void createManager() {
        try {
            server = new KVServer();
            server.start();
            HistoryManager historyManager = Managers.getDefaultHistory();
            manager = Managers.getDefault(historyManager);
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при создании менеджера");
        }
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    void shouldLoadTasks() {
        Task task1 = new Task("description1", "name1", Status.NEW, LocalDateTime.now(), Duration.between(LocalTime.MIN, LocalTime.of(0, 30, 00)));
        Task task2 = new Task("description2", "name2", Status.NEW, LocalDateTime.now(), Duration.between(LocalTime.MIN, LocalTime.of(01, 30, 00)));
        manager.addTask(task1);
        manager.addTask(task2);
        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getAllTasks(), list);
    }

    @Test
    void shouldLoadEpics() {
        Epic epic1 = new Epic("name","detail", Status.NEW);
        Epic epic2 = new Epic("name1","detail1", Status.NEW);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.getEpic(epic1.getId());
        manager.getEpic(epic2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getAllEpics(), list);
    }

    @Test
    void shouldLoadSubtasks() {
        Epic epic1 = new Epic("name","detail", Status.NEW);
        Subtask subtask1 = new Subtask("name1","detail1", Status.NEW, epic1.getId()
                , LocalDateTime.now(), Duration.between(LocalTime.MIN, LocalTime.of(0, 15, 00)));
        Subtask subtask2 = new Subtask("name2","detail2", Status.NEW, epic1.getId(),
               LocalDateTime.now(), Duration.between(LocalTime.MIN, LocalTime.of(0, 5, 00)));
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.getSubtask(subtask1.getId());
        manager.getSubtask(subtask2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getAllSubtasks(), list);
    }

}