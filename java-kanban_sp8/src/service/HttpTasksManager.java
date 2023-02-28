package service;

import com.google.gson.*;
import http.KVClient;
import model.Epic;
import model.Subtask;
import model.Task;
import util.LocalDateTimeTypeAdapter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class HttpTasksManager extends FileBackedTasksManager {
    static final String KEY_TASKS = "tasks";
    static final String KEY_SUBTASKS = "subtasks";
    static final String KEY_EPICS = "epics";
    static final String KEY_HISTORY = "history";
    private static final Gson gson =
            new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()).create();
    final KVClient client;

    public HttpTasksManager(HistoryManager historyManager, String path) throws IOException, InterruptedException {
        super(historyManager);
        client = new KVClient(path);

        JsonElement jsonTasks = JsonParser.parseString(client.load(KEY_TASKS));
        if (!jsonTasks.isJsonNull()) {
            JsonArray jsonTasksArray = jsonTasks.getAsJsonArray();
            for (JsonElement jsonTask : jsonTasksArray) {
                Task task = gson.fromJson(jsonTask, Task.class);
                this.addTask(task);
            }
        }

        JsonElement jsonEpics = JsonParser.parseString(client.load(KEY_EPICS));
        if (!jsonEpics.isJsonNull()) {
            JsonArray jsonEpicsArray = jsonEpics.getAsJsonArray();
            for (JsonElement jsonEpic : jsonEpicsArray) {
                Epic task = gson.fromJson(jsonEpic, Epic.class);
                this.addEpic(task);
            }
        }

        JsonElement jsonSubtasks = JsonParser.parseString(client.load(KEY_SUBTASKS));
        if (!jsonSubtasks.isJsonNull()) {
            JsonArray jsonSubtasksArray = jsonSubtasks.getAsJsonArray();
            for (JsonElement jsonSubtask : jsonSubtasksArray) {
                Subtask task = gson.fromJson(jsonSubtask, Subtask.class);
                this.addSubtask(task);
            }
        }

        JsonElement jsonHistoryList = JsonParser.parseString(client.load(KEY_HISTORY));
        if (!jsonHistoryList.isJsonNull()) {
            JsonArray jsonHistoryArray = jsonHistoryList.getAsJsonArray();
            for (JsonElement jsonTaskId : jsonHistoryArray) {
                int taskId = jsonTaskId.getAsInt();
                if (this.getSubtaskRepository().containsKey(taskId)) {
                    this.getSubtask(taskId);
                } else if (this.getEpicRepository().containsKey(taskId)) {
                    this.getEpic(taskId);
                } else if (this.getTaskRepository().containsKey(taskId)) {
                    this.getTask(taskId);
                }
            }
        }
    }

    public HttpTasksManager(HistoryManager historyManager, File file, KVClient client) {
        super(historyManager, file);
        this.client = client;
    }

    @Override
    public void save() {
        client.put(KEY_TASKS, gson.toJson(getTaskRepository().values()));
        client.put(KEY_SUBTASKS, gson.toJson(getSubtaskRepository().values()));
        client.put(KEY_EPICS, gson.toJson(getEpicRepository().values()));
        client.put(KEY_HISTORY, gson.toJson(this.getHistory()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList())));
    }
}
