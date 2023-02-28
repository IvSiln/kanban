package service;

import exception.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private  File file = null;
    private static final String TITLE = "id,type,name,status,detail,epicId,startTime, duration\n";


    public FileBackedTasksManager(HistoryManager historyManager) {
        super(historyManager);
    }
    public FileBackedTasksManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringBuilder str = new StringBuilder();

        if (history.isEmpty()) {
            return "";
        }

        for (Task task : history) {
            str.append(task.getId()).append(",");
        }

        if (str.length() != 0) {
            str.deleteCharAt(str.length() - 1);
        }

        return str.toString();
    }

    @Override
    public Task addTask(Task task) {
        super.addTask(task);
        save();
        return task;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void clearTask(int id) {
        super.clearTask(id);
        save();
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public Epic addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void clearEpic(int id) {
        super.clearEpic(id);
        save();
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void clearSubtask(int id) {
        super.clearSubtask(id);
        save();
    }

    @Override
    public void clearAllSubtask() {
        super.clearAllSubtask();
        save();
    }

    public void loadFromFile() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            var line = bufferedReader.readLine();
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                if (line.isEmpty()) break;

                Task task = fromString(line);

                if (task instanceof Epic) {
                    addEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    addSubtask((Subtask) task);
                } else {
                    addTask(task);
                }
            }

            var historyLine = bufferedReader.readLine();
            for (int id : historyFromString(historyLine)) {
                addHistoryFromFile(id);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении из файла");
        }
    }

    public void save() {
        try {
            if (Files.exists(file.toPath())) {
                Files.delete(file.toPath());
            }
            Files.createFile(file.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось найти файл для записи данных");
        }

        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(TITLE);

            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }

            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }

            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }

            writer.write("\n");
            writer.write(historyToString(getHistoryManager()));
        } catch (IOException exception) {
            throw new ManagerSaveException("Не удалось сохранить в файл", exception);
        }
    }

    private String toString(Task task) {
        Duration duration = (task.getDuration() == null ? Duration.ZERO : task.getDuration());
        String localDateTime = (task.getStartTime() == null ? "null" : task.getStartTime().toString());
        String[] generatingArray = {Integer.toString(task.getId()), getType(task).toString(), task.getName(),
                task.getStatus().toString(), task.getDetail(), getEpicId(task), localDateTime,
                String.valueOf(duration)};
        
        return String.join(",", generatingArray);
    }

    private String getEpicId(Task task) {
        if (task instanceof Subtask) {
            return Integer.toString(((Subtask) task).getEpicId());
        }
        return "";
    }

    private Type getType(Task task) {
        if (task instanceof Epic) {
            return Type.EPIC;
        } else if (task instanceof Subtask) return Type.SUBTASK;
        return Type.TASK;
    }

    private List<Integer> historyFromString(String historyLine) {
        List<Integer> restoredHistory = new ArrayList<>();
        if (historyLine != null) {
            String[] historyIdArray = historyLine.split(",");
            for (String id : historyIdArray) {
                restoredHistory.add(Integer.parseInt(id));
            }
        }
        return restoredHistory;
    }

    private Task fromString(String line) {
        String[] recovery = line.split(",");
        int id = Integer.parseInt(recovery[0]);
        String type = recovery[1];
        String name = recovery[2];
        Status status = Status.valueOf(recovery[3]);
        String detail = recovery[4];
        Integer epicId = type.equals("SUBTASK") ? Integer.parseInt(recovery[5]) : null;
        LocalDateTime startTime = LocalDateTime.parse(recovery[6]);
        Duration duration = Duration.parse(recovery[7]);

        if (type.equals("EPIC")) {
            var epic = new Epic(name, detail, status);
            epic.setId(id);
            epic.setStatus(status);
            return epic;
        } else if (type.equals("SUBTASK")) {
            var subtask = new Subtask(name, detail, status, epicId, startTime, duration);
            subtask.setId(id);
            return subtask;
        } else {
            var task = new Task(name, detail, status, startTime, duration);
            task.setId(id);
            return task;
        }
    }
}

