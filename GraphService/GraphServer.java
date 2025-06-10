import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.nio.file.Files;
import java.nio.file.Path;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;

public class GraphServer {

    public static void main(String[] args) throws Exception {
        int port = 8081;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/graph", GraphServer::handleGraphRequest);
        server.createContext("/graph/multi", GraphServer::handleMultiGraphRequest);


        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        System.out.println("GraphService escuchando en puerto " + port);
    }

    private static void handleGraphRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String query = exchange.getRequestURI().getQuery(); // ?crypto=bitcoin&hours=3
        if (query == null || !query.contains("crypto=")) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }

        String[] parts = query.split("&");
        String crypto = "bitcoin";
        int hours = 1;

        for (String part : parts) {
            if (part.startsWith("crypto=")) {
                crypto = part.split("=")[1];
            } else if (part.startsWith("hours=")) {
                hours = Integer.parseInt(part.split("=")[1]);
            }
        }

        List<GraphGenerator.DataPoint> data = DataReader.readCryptoHistory(crypto, hours);
        String outputPath = "output/" + crypto + "_" + hours + "h.png";

        GraphGenerator.generateGraph(crypto, data, outputPath);

        File imageFile = new File(outputPath);
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());

        exchange.getResponseHeaders().add("Content-Type", "image/png");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, imageBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(imageBytes);
        os.close();
    }

    private static void handleMultiGraphRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String query = exchange.getRequestURI().getQuery(); // ?cryptos=bitcoin,ethereum&hours=3
        if (query == null || !query.contains("cryptos=")) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }

        String[] parts = query.split("&");
        List<String> cryptos = List.of("bitcoin");
        int hours = 1;

        for (String part : parts) {
            if (part.startsWith("cryptos=")) {
                String raw = part.split("=")[1];
                cryptos = List.of(raw.split(","));
            } else if (part.startsWith("hours=")) {
                hours = Integer.parseInt(part.split("=")[1]);
            }
        }

        String filename = "multi_" + System.currentTimeMillis() + ".png";
        String outputPath = "output/" + filename;

        GraphGenerator.generateMultiGraph(cryptos, hours, outputPath);

        File imageFile = new File(outputPath);
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());

        exchange.getResponseHeaders().add("Content-Type", "image/png");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, imageBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(imageBytes);
        os.close();
    }

}
