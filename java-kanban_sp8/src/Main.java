import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.KVServer;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.HistoryManager;
import service.TaskManager;
import util.DurationTypeAdapter;
import util.LocalDateTimeTypeAdapter;
import util.Managers;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Main {

    public static void main(String[] args) throws IOException {
        KVServer server;
        try {
            final Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                    .create();

            server = new KVServer();
            server.start();
            HistoryManager historyManager = Managers.getDefaultHistory();
            TaskManager httpTaskManager = Managers.getDefault(historyManager);

            Task task1 = new Task("Разработать лифт до луны", "Космолифт", Status.NEW, LocalDateTime.now(), Duration.parse("PT1H15M"));

            httpTaskManager.addTask(task1);

            Epic epic1 = new Epic("Посадить дерево", "Дерево", Status.NEW);
            httpTaskManager.addEpic(epic1);

            Subtask subtask1 = new Subtask("Купить семена", "Семена", Status.NEW, epic1.getId(), LocalDateTime.now(), Duration.parse("PT1H15M"));
            httpTaskManager.addSubtask(subtask1);

            httpTaskManager.getTask(task1.getId());
            httpTaskManager.getEpic(epic1.getId());
            httpTaskManager.getSubtask(subtask1.getId());

            System.out.println("Печать всех задач");
            System.out.println(gson.toJson(httpTaskManager.getAllTasks()));
            System.out.println("Печать всех эпиков");
            System.out.println(gson.toJson(httpTaskManager.getAllEpics()));
            System.out.println("Печать всех подзадач");
            System.out.println(gson.toJson(httpTaskManager.getAllSubtasks()));
            System.out.println("Загруженный менеджер");
            System.out.println(httpTaskManager);
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

