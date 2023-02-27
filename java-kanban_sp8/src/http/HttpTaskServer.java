package http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.Subtask;
import model.Task;
import service.HttpTasksManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private final int PORT = 8080;
    private HttpTasksManager manager;
    private HttpServer httpServer;

    public HttpTaskServer() throws IOException {
        manager = new HttpTasksManager();
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        createContext(httpServer);
    }

    public HttpTaskServer(String saveKey) throws IOException {
        manager = new HttpTasksManager();
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        createContext(httpServer);
    }

    public HttpTaskServer(String newKey, String loadKey) throws IOException {
        manager = HttpTasksManager.loadFromJson(loadKey, newKey);
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        createContext(httpServer);
        start();
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

    public void start() {
        System.out.println("Запуск HttpTaskServer сервера на порту " + PORT);
        httpServer.start();
    }

    public void stop() {
        System.out.println("Остановка HttpTaskServer сервера на порту " + PORT);
        httpServer.stop(1);
    }

    private void createContext(HttpServer server) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();

        server.createContext("/tasks", (h) -> {
            JsonObject jsonObject = new JsonObject();
            try {
                System.out.println("\n/tasks");
                if ("GET".equals(h.getRequestMethod())) {
                    JsonArray priority = new JsonArray();
                    jsonObject.add("taskPriority", priority);
                    for (Task task : manager.getPrioritizedTasks()) {
                        priority.add(task.getId());
                    }
                    sendText(h, jsonObject.toString());
                } else {
                    System.out.println("/tasks ожидал GET - запрос, а получил " + h.getRequestMethod());
                    h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });

        server.createContext("/tasks/history", (h) -> {
            JsonObject jsonObject = new JsonObject();
            try {
                System.out.println("\n/tasks/history");
                if ("GET".equals(h.getRequestMethod())) {
                    JsonArray historyArray = new JsonArray();
                    jsonObject.add("history", priority);
                    for (Task task : manager.history) {
                        historyArray.add(task.getId());
                    }
                    sendText(h, jsonObject.toString());
                } else {
                    System.out.println("/tasks/history ожидал GET - запрос, а получил " + h.getRequestMethod());
                    h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });

        server.createContext("tasks/task", (h) -> {
            JsonObject jsonObject = new JsonObject();
            Integer id = getIntParametr(h.getRequestURI(), "id");
            try {
                System.out.println("\n/tasks/task");
                switch (h.getRequestMethod()) {
                    case "GET":
                        if (id == null) {
                            jsonObject.add("tasks", gson.toJsonTree(manager.getAallTaskList()));
                            sendText(h, jsonObject.toString());
                        } else sendText(h, gson.toJson(manager.getTask(id)));
                        break;
                    case "POST":
                        manager.addTask(gson.fromJson((JsonParser.parseString(readText(h)), Task.class));
                        h.sendResponseHeaders(200, 0);
                        break;
                    case "DELETE":
                        manager.clearTask(id);
                        h.sendResponseHeaders(200, 0);
                        break;
                    default:
                }
                System.out.println("/tasks/task ожидал GET, POST, DELETE - запрос, а получил " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            } finally {
                h.close();
            }
        });
        server.createContext("/tasks/subtask", (h) -> {
            Integer id = getIntParametr(h.getRequestURI(), "id");
            ;
            try {
                System.out.println("\n/tasks/subtask");
                switch (h.getRequestMethod()) {
                    case "GET":     //Отправить подзадачу по запросу
                        if (id != null) {
                            sendText(h, gson.toJson((Subtask) manager.getTask(id)));
                        } else {
                            h.sendResponseHeaders(404, 0);
                        }
                        break;
                    case "POST":    //Создать задачу на основе полученного Json
                        Subtask subTask = gson.fromJson(JsonParser.parseString(readText(h)), Subtask.class);
                        manager.addSubtask(subtask);
                        //manager.addTask(gson.fromJson(JsonParser.parseString(readText(h)), SubTask.class));
                        h.sendResponseHeaders(200, 0);
                        break;
                    case "DELETE":  //Удаление подзадачи
                        if (id != null) {
                            manager.clearSubtask(id);
                        }
                        h.sendResponseHeaders(200, 0);
                        break;
                    default:
                        System.out.println("/tasks/subtask ждёт GET, POST, DELETE - запрос, а получил " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });

        server.createContext("/tasks/subtask/epic", (h) -> {
            Integer id = getIntParametr(h.getRequestURI(), "id");
            try {
                System.out.println("\n/tasks/subtask/epic");
                if ("GET".equals(h.getRequestMethod())) {
                    if (id != null) {
                        sendText(h, gson.toJson(((Subtask) manager.getSubtask(id)).getEpic()));
                    } else {
                        h.sendResponseHeaders(404, 0);
                    }
                } else {
                    System.out.println("/tasks/subtask/epic ждёт GET - запрос, а получил " + h.getRequestMethod());
                    h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });

        server.createContext("/tasks/epic", (h) -> {
            Integer id = getIntParametr(h.getRequestURI(), "id");
            try {
                System.out.println("\n/tasks/epic");
                switch (h.getRequestMethod()) {
                    case "GET":     //Отправить эпик по запросу
                        if (id != null) {
                            sendText(h, gson.toJson((Epic) manager.getEpic(id)));
                        } else {
                            h.sendResponseHeaders(404, 0);
                        }
                        break;
                    case "POST":    //Создать эпика на основе полученного Json
                        String body = readText(h);
                        if (body != null) {
                            Epic epic = gson.fromJson(JsonParser.parseString(body), Epic.class);
                            manager.addTask(epic);
                            //Коррекция подзадач (если они пришли в составе эпика) после десериализации
                            for (Subtask subtask : epic.getSubtasksList()) {
                                subtask.setEpic(epic);  //Восстановление обратной связи с эпиком
                                manager.getAllTasksList().put(subtask.getId(), subtask);    //Прописывание подзадачи в общем списке менеджера
                            }
                        } else {
                            System.out.println("Пустые данные для создания эпика");
                        }
                        h.sendResponseHeaders(200, 0);
                        break;
                    case "DELETE":  //Удаление эпика (с подзадачами)
                        if (id != null) {
                            manager.clearEpic(id);
                        }
                        h.sendResponseHeaders(200, 0);
                        break;
                    default:
                        System.out.println("/tasks/epic ждёт GET, POST, DELETE - запрос, а получил " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });
    }

    private Integer getIntParametr(URI uri, String name) {
        if (uri != null && name != null) {
            String query = uri.getQuery();
            if (query != null) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    String[] param = pair.split("=");
                    if (param.length >= 1 && name.equals(param[0]))
                        return Integer.parseInt(param[1]);
                }
            }
        }
        return null;
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), "UTF-8");
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes("UTF-8");
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

}


