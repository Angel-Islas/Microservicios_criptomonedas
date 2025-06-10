import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import org.json.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Executors;

public class RegressionServer {
    private static final String DB_FILE = "../shared/data/crypto_data.json";

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8082), 0);
        server.createContext("/regression", RegressionServer::handleRequest);
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        System.out.println("RegressionService escuchando en puerto 8082");
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String query = exchange.getRequestURI().getQuery(); // crypto=bitcoin&start=7&end=9
        if (query == null || !query.contains("crypto=") || !query.contains("start=") || !query.contains("end=")) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }

        Map<String, String> params = new HashMap<>();
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=");
            params.put(kv[0], kv[1]);
        }

        String crypto = params.get("crypto");
        int startHour = Integer.parseInt(params.get("start"));
        int endHour = Integer.parseInt(params.get("end"));

        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();

        try {
            String content = new String(Files.readAllBytes(Paths.get(DB_FILE)));
            JSONArray log = new JSONArray(content);

            int index = 0;
            for (int i = 0; i < log.length(); i++) {
                JSONObject obj = log.getJSONObject(i);
                String timestamp = obj.getString("timestamp");
                JSONObject data = obj.getJSONObject("data");

                int hour = LocalTime.parse(timestamp.substring(11, 16)).getHour();

                if (hour >= startHour && hour < endHour && data.has(crypto)) {
                    x.add((double) index);
                    y.add(data.getJSONObject(crypto).getDouble("usd"));
                    index++;
                }
            }

            if (x.size() < 2) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            double[] coef = RegressionCalculator.calculateLinearRegression(x, y);
            String outputPath = "output/" + crypto + "_regression.png";

            RegressionGraphGenerator.generate(crypto, x, y, coef, outputPath);

            byte[] imageBytes = Files.readAllBytes(Paths.get(outputPath));
            exchange.getResponseHeaders().add("Content-Type", "image/png");
            exchange.getResponseHeaders().add("X-Equation", String.format("y = %.2fx + %.2f", coef[0], coef[1]));
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, imageBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(imageBytes);
            os.close();

        } catch (Exception e) {
            exchange.sendResponseHeaders(500, -1);
            e.printStackTrace();
        }
    }
}
