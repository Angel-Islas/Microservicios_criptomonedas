import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class RegressionServer {
    private static final String ENDPOINT_REGRESSION = "/regression";
    private static final int PORT = 8082;

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext(ENDPOINT_REGRESSION, RegressionServer::handleRegressionRequest);

        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        System.out.println("RegressionService escuchando en puerto " + PORT);
    }

    private static void handleRegressionRequest(HttpExchange exchange) throws IOException {
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
        String name_crypto = "bitcoin";
        int hours = 1;

        for (String part : parts) {
            if (part.startsWith("crypto=")) {
                name_crypto = part.split("=")[1];
            } else if (part.startsWith("hours=")) {
                hours = Integer.parseInt(part.split("=")[1]);
            }
        }

        List<DataPoint> data = DataReader.readCryptoHistory(name_crypto, hours);

        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            x.add((double) i);
            y.add(data.get(i).price);
        }

        double[] coef = RegressionCalculator.calculateLinearRegression(x, y);

        String outputPath = "output/" + name_crypto + "_" + hours + "h_regression.png";
        RegressionGraphGenerator.generate(name_crypto, x, y, coef, outputPath);

        File imageFile = new File(outputPath);
        byte[] imageBytes = java.nio.file.Files.readAllBytes(imageFile.toPath());
        String imageBase64 = java.util.Base64.getEncoder().encodeToString(imageBytes);

        String equation = "y = " + coef[0] + "x + " + coef[1];
        String jsonResponse = String.format("{\"equation\":\"%s\",\"image_base64\":\"%s\"}", equation, imageBase64);

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        byte[] responseBytes = jsonResponse.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}
