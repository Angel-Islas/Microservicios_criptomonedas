import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.*;

public class DatabaseSeeder {

    private static final String FILE_PATH = "../shared/data/crypto_data.json";

    public static void run() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS cryptos (" +
                                "id SERIAL PRIMARY KEY, " +
                                "name VARCHAR(50), " +
                                "price DECIMAL(18,8), " +
                                "timestamp TIMESTAMP);");
                System.out.println("Tabla 'cryptos' creada o ya existe.");
            }

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM cryptos")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("La tabla ya tiene datos. No se hará precarga.");
                    return;
                }
            }

            File file = new File(FILE_PATH);
            if (!file.exists()) {
                System.out.println("Archivo JSON no encontrado: " + FILE_PATH);
                return;
            }

            String content = new String(Files.readAllBytes(file.toPath()));
            JSONArray snapshots = new JSONArray(content);

            for (int i = 0; i < snapshots.length(); i++) {
                JSONObject entry = snapshots.getJSONObject(i);
                JSONObject data = entry.getJSONObject("data");
                String tsRaw = entry.getString("timestamp");
                LocalDateTime ldt = LocalDateTime.parse(tsRaw.split("\\.")[0]);
                Timestamp ts = Timestamp.valueOf(ldt);

                for (String name : data.keySet()) {
                    double price = data.getJSONObject(name).getDouble("usd");
                    Crypto crypto = new Crypto(name, price, ts);
                    CryptoRepository.create(crypto);
                }
            }

            System.out.println("Base de datos precargada usando CryptoRepository.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Iniciando DatabaseSeeder...");
        run();
        System.out.println("DatabaseSeeder finalizado.");
    }
}
