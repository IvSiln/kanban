package http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVClient {
    private final int PORT = 8078;    //Порт для обращения к серверу
    private final String API_KEY;     //Ключ доступа к серверу
    private HttpClient client;
    private String url;

    //Конструктор класса
    public KVClient(String url){
        this.url = url; //Ожидается строка вида: localhost
        client = HttpClient.newHttpClient();
        API_KEY = register();
    }

    //Метод для проверки работы клиента с сервером KVServer
    public static void main(String[] args){
        System.out.println("Создание экземпляра клиента KVClient - создание соединения с KVServer и запрос API_KEY");
        KVClient testKVClient = new KVClient("localhost");

        //Сравнение полученного API_KEY с результатом повторного запроса
        System.out.println("От KVServer получен API_KEY = " + testKVClient.API_KEY);
        System.out.println("Повторный запрос API_KEY через register() для сравнения - " + testKVClient.register());

        System.out.println("\nЗагрузка тестовых данных на сервер - {\"name\": \"Тестовый Json.\"}");
        testKVClient.put("test_key", "{\"name\": \"Тестовый Json.\"}");

        System.out.println("Получение тестовых данных с сервера");
        System.out.println("load(\"test_key\") = " + testKVClient.load("test_key"));

        System.out.println("\nОбновление тестовых данных на сервере по существующему ключу \"test_key\" " +
                "на {\"name\": \"Изменённый тестовый Json.\"}");
        testKVClient.put("test_key", "{\"name\": \"Изменённый тестовый Json.\"}");

        System.out.println("Получение тестовых данных с сервера для сравнения");
        System.out.println("load(\"test_key\") = " + testKVClient.load("test_key"));
    }

    //Регистрация соединения (получение уникального кода соединения)
    public String register(){
        URI uri = URI.create("http://" + url + ":" + PORT + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);

            //System.out.println(response.statusCode());
            if (response.statusCode() == 200)
                return response.body();
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка: " + e.getMessage());
        }

        return "";
    }

    //Сохранение данных по сети
    //Метод void put(String key, String json) должен сохранять состояние менеджера задач через запрос POST /save/<ключ>?API_KEY=
    public void put(String key, String json){
        URI uri = URI.create("http://" + url + ":" + PORT + "/save/" + key + "?API_KEY=" + API_KEY);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);

            if (response.statusCode() != 200)
                System.out.println("Код ответа: " + response.statusCode());
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка: " + e.getMessage());
        }
    }

    //Получение данных из базы
    //Метод String load(String key) должен возвращать состояние менеджера задач через запрос GET /load/<ключ>?API_KEY=
    public String load(String key){
        URI uri = URI.create("http://" + url + ":" + PORT + "/load/" + key + "?API_KEY=" + API_KEY);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);

            if (response.statusCode() == 200)
                return response.body();
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка: " + e.getMessage());
        }

        return "";
    }
}
