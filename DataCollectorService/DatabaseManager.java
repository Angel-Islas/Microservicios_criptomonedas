import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.json.JSONObject;

public class DatabaseManager {

    public static boolean save(JSONObject prices) {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

        try {
            for (String name : prices.keySet()) {
                double price = prices.getDouble(name);
                Crypto crypto = new Crypto(name, price, timestamp);
                CryptoRepository.create(crypto);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
