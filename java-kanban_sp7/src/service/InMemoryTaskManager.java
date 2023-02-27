package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private static int id;
    private final HistoryManager historyManager;
    private final Map<Integer, Task> taskRepository = new HashMap<>();
    private final Map<Integer, Epic> epicRepository = new HashMap<>();
    private final Map<Integer, Subtask> subtaskRepository = new HashMap<>();
    private final Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime);
    private final Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public static int generateId() {
        return ++id;
    }

    private void addPrioritizedTask(Task task) {
        prioritizedTasks.add(task);
        validatePriority();
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public boolean isRightTime(Task task) {
        List<Task> sortedTask = List.copyOf(prioritizedTasks);
        int sizeTimeNull = 0;
        if (!sortedTask.isEmpty()) {
            for (Task forSave : sortedTask) {
                if (forSave.getStartTime() != null && forSave.getFinishTime() != null) {
                    return isEarly(task, forSave);
                } else if (isLater(task, forSave)) return true;
                else sizeTimeNull++;
            }
            return sizeTimeNull == sortedTask.size();
        } else return true;
    }

    private List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    public boolean isEarly(Task first, Task second) {
        return (first.getStartTime().isBefore(second.getStartTime()) && first.getFinishTime().isBefore(second.getFinishTime()));
    }

    public boolean isLater(Task first, Task second) {
        return (first.getStartTime().isAfter(second.getStartTime()) && first.getFinishTime().isAfter(second.getFinishTime()));
    }

    private void validatePriority() {
        List<Task> tasks = getPrioritizedTasks();

        for (int i = 1; i < tasks.size(); i++) {
            Task task = tasks.get(i);

            boolean taskHasIntersections = isRightTime(task);

            if (taskHasIntersections) {
                throw new CheckForException("Внимание задачи №" + task.getId() + " и №" + tasks.get(i - 1) + "пересекаются по времени");
            }
        }
    }

    @Override
    public Task addTask(Task task) {
        if (task == null) return null;
        int newId = generateId();
        task.setId(newId);
        addPrioritizedTask(task);
        taskRepository.put(newId, task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && getTaskRepository().containsKey(task.getId())) {
            addPrioritizedTask(task);
            taskRepository.put(task.getId(), task);
        } else System.out.println("Task не обнаружен");
    }

    @Override
    public Task getTask(int id) {
        Task task = taskRepository.getOrDefault(id, null);
        if (task != null) historyManager.add(task);
        return task;
    }

    @Override
    public Map<Integer, Task> getTaskRepository() {
        if (taskRepository.size() == 0) {
            System.out.println("Список задач пуст");
            return Collections.emptyMap();
        }
        return taskRepository;
    }

    @Override
    public void clearTask(int id) {
        if (taskRepository.containsKey(id)) {
            prioritizedTasks.removeIf(task -> task.getId() == id);
            taskRepository.remove(id);
            historyManager.remove(id);
        } else System.out.println("Список задач пуст");
    }

    public void clearAllTasks() {
        taskRepository.clear();
        prioritizedTasks.clear();
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && getEpicRepository().containsKey(epic.getId())) {
            epicRepository.put(epic.getId(), epic);
            epic.getActualStatus(epic);
            epic.calculateEpicTime();
        } else System.out.println("Список эпиков пуст");
    }

    @Override
    public Epic addEpic(Epic epic) {
        if (epic != null) {
            int newId = generateId();
            epic.setId(newId);
            epic.setStatus(Status.NEW);
            if (!epic.getSubtasksList().isEmpty()) epic.calculateEpicTime();
            epicRepository.put(epic.getId(), epic);
            return epic;
        }
        return null;
    }

    @Override
    public Epic getEpic(int id) {
        var epic = epicRepository.getOrDefault(id, null);
        if (epic != null) historyManager.add(epic);
        return epic;
    }

    @Override
    public Map<Integer, Epic> getEpicRepository() {
        if (epicRepository.size() == 0) {
            System.out.println("Список эпиков пуст");
            return Collections.emptyMap();
        }
        return epicRepository;
    }

    @Override
    public void clearEpic(int id) {
        if (epicRepository.containsKey(id)) {
            var epic = epicRepository.get(id);
            if (epic != null) {
                for (var subtaskList : epic.getSubtasksList()) {
                    subtaskRepository.remove(subtaskList.getId());
                    historyManager.remove(subtaskList.getId());
                    prioritizedTasks.removeIf(task -> Objects.equals(task.getId(), subtaskList.getId()));
                }
                epicRepository.remove(id);
                historyManager.remove(id);
            } else System.out.println("Эпик не найден");
        }
    }

    @Override
    public void clearAllEpics() {
        epicRepository.clear();
        subtaskRepository.clear();
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        if (subtask == null) return null;
        int newSubtaskId = generateId();
        subtask.setId(newSubtaskId);
        Epic epic = getEpicRepository().get(subtask.getEpicId());
        if (epic != null) {
            addPrioritizedTask(subtask);
            getSubtaskRepository().put(newSubtaskId, subtask);
            epic.addSubtask(subtask);
            epic.getActualStatus(epic);
            epic.calculateEpicTime();
            return subtask;
        } else {
            System.out.println("Эпик не найден");
            return null;
        }
    }

    public List<Subtask> getAllSubtasksByEpicId(int id) {
        if (getEpicRepository().containsKey(id)) {
            List<Subtask> subtasksNew = new ArrayList<>();
            Epic epic = getEpicRepository().get(id);
            for (int i = 0; i < epic.getSubtasksList().size(); i++) {
                subtasksNew.add(getSubtaskRepository().get(epic.getSubtasksList().get(i)));
            }
            return subtasksNew;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && getSubtaskRepository().containsKey(subtask.getId())) {
            addPrioritizedTask(subtask);
            getSubtaskRepository().put(subtask.getId(), subtask);
            Epic epic = getEpicRepository().get(subtask.getEpicId());
            epic.getActualStatus(epic);
            epic.calculateEpicTime();
        } else {
            System.out.println("Сабтаск не найден");
        }
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtaskRepository.getOrDefault(id, null);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Map<Integer, Subtask> getSubtaskRepository() {
        return subtaskRepository;
    }

    @Override
    public void clearSubtask(int id) {
        Subtask subtask = getSubtaskRepository().get(id);
        if (subtask != null) {
            Epic epic = epicRepository.get(subtask.getEpicId());
            epic.getSubtasksList().remove(id);
            epic.getActualStatus(epic);
            subtaskRepository.remove(id);
            historyManager.remove(id);
        }
    }

    public void clearAllSubtask() {
        for (var epic : getEpicRepository().values()) {
            for (var subtask : epic.getSubtasksList()) {
                prioritizedTasks.remove(subtask);
                getSubtaskRepository().remove(subtask.getId());
                historyManager.remove(subtask.getId());
            }
            epic.getSubtasksList().clear();
        }
    }

    @Override
    public List<Task> getAllTaskReport() {
        List<Task> allTaskReport = new ArrayList<>();

        allTaskReport.addAll(taskRepository.values());
        allTaskReport.addAll(epicRepository.values());
        allTaskReport.addAll(subtaskRepository.values());
        return allTaskReport;
    }

    @Override
    public void clearAllTask() {
        getTaskRepository().clear();
        getEpicRepository().clear();
        getSubtaskRepository().clear();
    }

    @Override
    public List<Subtask> getListSubtask(Epic epic) {
        if (getSubtaskRepository().size() == 0) {
            System.out.println("Список сабтасков пуст");
            return Collections.emptyList();
        }
        return new ArrayList<>(getSubtaskRepository().values());
    }

    @Override
    public List<Task> getAllTasks() {
        if (taskRepository.size() == 0) {
            System.out.println("Список тасков пуст");
            return Collections.emptyList();
        }
        return new ArrayList<>(taskRepository.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        if (epicRepository.size() == 0) {
            System.out.println("Список эпиков пуст");
            return Collections.emptyList();
        }
        return new ArrayList<>(epicRepository.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        if (subtaskRepository.size() == 0) {
            System.out.println("Список сабтасков пуст");
            return Collections.emptyList();
        }
        return new ArrayList<>(subtaskRepository.values());
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public void addHistoryFromFile(int id) {
        if (epicRepository.containsKey(id)) {
            historyManager.add(epicRepository.get(id));
        } else if (subtaskRepository.containsKey(id)) {
            historyManager.add(subtaskRepository.get(id));
        } else if (taskRepository.containsKey(id)) {
            historyManager.add(taskRepository.get(id));
        }
    }

    private static class CheckForException extends RuntimeException {
        public CheckForException(String message) {
            super(message);
        }
    }
}


