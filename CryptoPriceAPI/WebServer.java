import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.json.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;

public class WebServer {
    private static final String STATUS_ENDPOINT = "/status";
    private static final String PRICES_ENDPOINT = "/api/prices";
    private static final String HISTORY_ENDPOINT = "/api/history";

    private final int port;
    private HttpServer server;
    private static final String DB_FILE = "../shared/data/crypto_data.json";

    public static void main(String[] args) {
        int serverPort = 8080;
        if (args.length == 1) {
            serverPort = Integer.parseInt(args[0]);
        }

        WebServer webServer = new WebServer(serverPort);
        webServer.startServer();

        System.out.println("Servidor escuchando en el puerto " + serverPort);
    }

    public WebServer(int port) {
        this.port = port;
    }

    public void startServer() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        server.createContext(STATUS_ENDPOINT, this::handleStatusCheckRequest);
        server.createContext(PRICES_ENDPOINT, this::handlePricesRequest);
        server.createContext(HISTORY_ENDPOINT, this::handleHistoryRequest);

        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
    }

    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String response = "El servidor está vivo\n";
        sendResponse(response.getBytes(), exchange);
    }

    private void handlePricesRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        JSONArray log = readDatabase();
        if (log.length() == 0) {
            sendResponse("[]".getBytes(), exchange);
            return;
        }

        JSONObject latestSnapshot = log.getJSONObject(log.length() - 1);
        JSONObject data = latestSnapshot.getJSONObject("data");

        sendResponse(data.toString(2).getBytes(), exchange);
    }

    private void handleHistoryRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String query = exchange.getRequestURI().getQuery(); // e.g., ?crypto=bitcoin&hours=3
        if (query == null || !query.contains("crypto=") || !query.contains("hours=")) {
            exchange.sendResponseHeaders(400, -1); // Bad request
            return;
        }

        String[] params = query.split("&");
        String crypto = "", hours = "1";

        for (String param : params) {
            if (param.startsWith("crypto=")) {
                crypto = param.split("=")[1];
            } else if (param.startsWith("hours=")) {
                hours = param.split("=")[1];
            }
        }

        int numHours = Integer.parseInt(hours);
        JSONArray log = readDatabase();

        JSONArray history = new JSONArray();
        int count = 0;
        for (int i = log.length() - 1; i >= 0 && count < numHours * 60; i--) {
            JSONObject entry = log.getJSONObject(i);
            JSONObject prices = entry.getJSONObject("data");

            if (prices.has(crypto)) {
                JSONObject record = new JSONObject();
                record.put("timestamp", entry.getString("timestamp"));
                record.put("price", prices.getJSONObject(crypto).getDouble("usd"));
                history.put(record);
                count++;
            }
        }

        sendResponse(history.toString(2).getBytes(), exchange);
    }

    private JSONArray readDatabase() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(DB_FILE)));
            return new JSONArray(content);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.flush();
        outputStream.close();
        exchange.close();
    }
}
