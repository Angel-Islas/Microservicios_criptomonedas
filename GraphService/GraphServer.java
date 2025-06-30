import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class GraphServer {
    private static final String ENDPOINT_GRAPH = "/graph";
    private static final String ENDPOINT_MULTI_GRAPH = "/graph/multi";
    private static final int PORT = 8081;

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext(ENDPOINT_GRAPH, GraphServer::handleGraphRequest);
        server.createContext(ENDPOINT_MULTI_GRAPH, GraphServer::handleMultiGraphRequest);

        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        System.out.println("GraphService escuchando en puerto " + PORT);
    }

    private static void handleGraphRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String query = exchange.getRequestURI().getQuery(); // ?crypto=bitcoin&hours=3
        String crypto = "bitcoin";
        int hours = 1;

        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("crypto=")) {
                    crypto = param.split("=")[1];
                } else if (param.startsWith("hours=")) {
                    hours = Integer.parseInt(param.split("=")[1]);
                }
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
        List<String> cryptos = List.of("bitcoin");
        int hours = 1;

        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("cryptos=")) {
                    String raw = param.split("=")[1];
                    cryptos = List.of(raw.split(","));
                } else if (param.startsWith("hours=")) {
                    hours = Integer.parseInt(param.split("=")[1]);
                }
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
