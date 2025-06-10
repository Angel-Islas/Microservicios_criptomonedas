import java.io.*;
import java.time.LocalDateTime;
import org.json.*;

public class DatabaseManager {
    private static final String FILE_PATH = "../shared/data/crypto_data.json";

    public static void save(JSONObject prices) {
        JSONObject snapshot = new JSONObject();
        snapshot.put("timestamp", LocalDateTime.now().toString());
        snapshot.put("data", prices);

        JSONArray log;
        try {
            File file = new File(FILE_PATH);
            if (file.exists()) {
                String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                log = new JSONArray(content);
            } else {
                log = new JSONArray();
            }
        } catch (IOException e) {
            log = new JSONArray();
        }

        log.put(snapshot);

        try (FileWriter file = new FileWriter(FILE_PATH)) {
            file.write(log.toString(2));
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
}
