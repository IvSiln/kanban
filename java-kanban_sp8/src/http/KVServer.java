package http;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * Постман: https://www.getpostman.com/collections/a83b61d9e1c81c10575c
 */
public class KVServer {
	public static final int PORT = 8078;
	private final String API_KEY;
	private HttpServer server;
	private Map<String, String> data = new HashMap<>();

	public KVServer() throws IOException {
		API_KEY = generateApiKey();
		server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
		createContext(server);
	}

	private void createContext(HttpServer server) {
		server.createContext("/register", (h) -> {
			try {
				System.out.println("\n/register");
				switch (h.getRequestMethod()) {
					case "GET":
						sendText(h, API_KEY);
						break;
					default:
						System.out.println("/register ждёт GET-запрос, а получил " + h.getRequestMethod());
						h.sendResponseHeaders(405, 0);
				}
			} finally {
				h.close();
			}
		});

		server.createContext("/save", (h) -> {
			try {
				System.out.println("\n/save");
				if (!hasAuth(h)) {
					System.out.println("Запрос неавторизован, нужен параметр в query API_KEY со значением апи-ключа");
					h.sendResponseHeaders(403, 0);
					return;
				}
				switch (h.getRequestMethod()) {
					case "POST":
						String key = h.getRequestURI().getPath().substring("/save/".length());
						if (key.isEmpty()) {
							System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
							h.sendResponseHeaders(400, 0);
							return;
						}
						String value = readText(h);
						if (value.isEmpty()) {
							System.out.println("Value для сохранения пустой. value указывается в теле запроса");
							h.sendResponseHeaders(400, 0);
							return;
						}
						data.put(key, value);
						System.out.println("Значение для ключа " + key + " успешно обновлено!");
						h.sendResponseHeaders(200, 0);
						break;
					default:
						System.out.println("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
						h.sendResponseHeaders(405, 0);
				}
			} finally {
				h.close();
			}
		});

		server.createContext("/load", (h) -> {
			try {
				System.out.println("\n/load");
				if (!hasAuth(h)) {
					System.out.println("Запрос не авторизован, нужен параметр в query API_KEY со значением апи-ключа");
					h.sendResponseHeaders(403, 0);
					return;
				}
				switch (h.getRequestMethod()) {
					case "GET":
						String key = h.getRequestURI().getPath().substring("/load/".length());
						if (key == null || key.trim().isEmpty()) {
							System.out.println("Key для загрузки - пустой. key указывается в пути: /save/{key}");
							h.sendResponseHeaders(400, 0);
							return;
						}

						if (data.containsKey(key)) {
							h.sendResponseHeaders(200, 0);
							try (OutputStream os = h.getResponseBody()) {
								os.write(data.get(key).getBytes());
							}
						} else {
							System.out.println("Key для загрузки '" + key + "' - не найден.");
							h.sendResponseHeaders(404, 0);
							return;
						}
						break;
					default:
						System.out.println("/load ждёт GET-запрос, а получил " + h.getRequestMethod());
						h.sendResponseHeaders(405, 0);
				}
			} finally {
				h.close();
			}
		});
	}

	public static void main(String[] args) throws IOException {
		new KVServer().start();
	}

	public void start() {
		System.out.println("Запуск KVServer сервера на порту " + PORT);
		System.out.println("Открой в браузере http://localhost:" + PORT + "/");
		System.out.println("API_KEY: " + API_KEY);
		server.start();
	}

	public void stop() {
		System.out.println("Остановка сервера KVServer на порту " + PORT);
		server.stop(1);
	}

	private String generateApiKey() {
		return "" + System.currentTimeMillis();
	}

	protected boolean hasAuth(HttpExchange h) {
		String rawQuery = h.getRequestURI().getRawQuery();
		return rawQuery != null && (rawQuery.contains("API_KEY=" + API_KEY) || rawQuery.contains("API_KEY=DEBUG"));
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