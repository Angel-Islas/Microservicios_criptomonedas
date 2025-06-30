import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class WebServer {
    private static final String STATUS_ENDPOINT = "/status";
    private static final String PRICES_ENDPOINT = "/api/prices";

    private final int port;
    private HttpServer server;

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

        try {
            List<Crypto> snapshot = CryptoRepository.findLatestSnapshot();
            if (snapshot.isEmpty()) {
                sendResponse("{}".getBytes(), exchange);
                return;
            }

            JSONObject prices = new JSONObject();
            for (Crypto c : snapshot) {
                JSONObject priceInfo = new JSONObject();
                priceInfo.put("usd", c.price);
                prices.put(c.name, priceInfo);
            }

            sendResponse(prices.toString(2).getBytes(), exchange);
        } catch (SQLException e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Access-Control-Allow-Origin", "*");

        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(responseBytes);
        }
        exchange.close();
    }
}
