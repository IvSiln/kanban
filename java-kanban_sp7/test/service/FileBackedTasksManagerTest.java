package service;

import exception.ManagerSaveException;
import model.Epic;
import model.Status;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Managers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest {
    public static final Path path = Path.of("test.history.csv");
    File file = new File(String.valueOf(path));

    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTasksManager((Managers.getDefaultHistory()), file);
    }

    @AfterEach
    public void afterEach() {
        try {
            Files.delete(path);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void shouldCorrectlySaveLoad() {
        Task task = new Task("name", "detail", Status.NEW);
        manager.addTask(task);
        Epic epic = new Epic("name", "detail", Status.NEW);
        manager.addEpic(epic);
        assertEquals(List.of(task), manager.getAllTasks());
        assertEquals(List.of(epic), manager.getAllEpics());
    }

    @Test
    void shouldSaveAndLoadEmptyMap() {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
        fileBackedTasksManager.save();
        fileBackedTasksManager.loadFromFile(file);
        assertEquals(Collections.EMPTY_LIST, manager.getAllTaskReport());
    }

    @Test
    void shouldTryCatchFromSavedWork() {
        File file1 = new File("");
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file1);
        Throwable thrown = assertThrows(ManagerSaveException.class, fileBackedTasksManager::save);
        assertNotNull(thrown.getMessage());
    }

    @Test
    void shouldTryCatchFromLoadWork() {
        File file1 = new File("");
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file1);
        Throwable thrown = assertThrows(ManagerSaveException.class, () -> fileBackedTasksManager.loadFromFile(file1));
        assertNotNull(thrown.getMessage());
    }

    @Test
    void shouldSaveAndLoadEmptyHistory() {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
        fileBackedTasksManager.save();
        fileBackedTasksManager.loadFromFile(file);
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }
}