package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;
import java.util.Map;

public interface TaskManager {
    Map<Integer, Task> getTaskRepository();

    Map<Integer, Subtask> getSubtaskRepository();

    Task addTask(Task task);

    Epic addEpic(Epic epic);

    void clearAllEpics();

    Subtask addSubtask(Subtask subtask);

    List<Task> getAllTaskReport();

    void clearAllTask();

    void clearAllSubtask();

    Task getTask(int taskId);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    List<Task> getHistory();

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void clearTask(int id);

    Map<Integer, Epic> getEpicRepository();

    void clearEpic(int id);

    void clearSubtask(int id);

    List<Subtask> getListSubtask(Epic epic);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    List<Task> getPrioritizedTasks();
}