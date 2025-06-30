import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataReader {
    public static List<DataPoint> readCryptoHistory(String crypto, int hours) {
        List<DataPoint> list = new ArrayList<>();
        try {
            List<Crypto> registros = CryptoRepository.findByNamesAndRecentHours(List.of(crypto), hours);
            for (Crypto c : registros)
                list.add(new DataPoint(c.timestamp.toLocalDateTime(), c.price));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static java.util.Map<String, List<DataPoint>> readMultipleCryptoHistory(List<String> cryptos, int hours) {
        java.util.Map<String, List<DataPoint>> result = new java.util.HashMap<>();
        try {
            List<Crypto> registros = CryptoRepository.findByNamesAndRecentHours(cryptos, hours);
            for (Crypto c : registros) {
                String key = c.name;
                result.putIfAbsent(key, new ArrayList<>());
                result.get(key).add(new DataPoint(c.timestamp.toLocalDateTime(), c.price));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
