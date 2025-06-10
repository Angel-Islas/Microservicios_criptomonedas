import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DataReader {
    private static final String DB_FILE = "../shared/data/crypto_data.json";

    public static List<GraphGenerator.DataPoint> readCryptoHistory(String crypto, int hours) {
        List<GraphGenerator.DataPoint> list = new ArrayList<>();

        try {
            String content = new String(Files.readAllBytes(Paths.get(DB_FILE)));
            JSONArray log = new JSONArray(content);

            int count = 0;
            for (int i = log.length() - 1; i >= 0 && count < hours * 60; i--) {
                JSONObject entry = log.getJSONObject(i);
                JSONObject data = entry.getJSONObject("data");
                if (data.has(crypto)) {
                    double price = data.getJSONObject(crypto).getDouble("usd");
                    String timestamp = entry.getString("timestamp");
                    list.add(new GraphGenerator.DataPoint(timestamp, price));
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
