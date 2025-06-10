import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DataReader {
    private static final String DB_FILE = "../shared/data/crypto_data.json";

    public static List<DataPoint> readCryptoHistory(String crypto, int hours) {
        List<DataPoint> list = new ArrayList<>();

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
                    list.add(new DataPoint(timestamp, price));
                    count++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<DataPoint> readCryptoHistoryBetweenHours(String crypto, int startHour, int endHour) {
        List<DataPoint> list = new ArrayList<>();

        try {
            String content = new String(Files.readAllBytes(Paths.get(DB_FILE)));
            JSONArray log = new JSONArray(content);

            for (int i = 0; i < log.length(); i++) {
                JSONObject obj = log.getJSONObject(i);
                String timestamp = obj.getString("timestamp");
                JSONObject data = obj.getJSONObject("data");

                int hour = Integer.parseInt(timestamp.substring(11, 13));

                if (hour >= startHour && hour < endHour && data.has(crypto)) {
                    double price = data.getJSONObject(crypto).getDouble("usd");
                    list.add(new DataPoint(timestamp, price));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
