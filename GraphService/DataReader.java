import java.sql.SQLException;
import java.util.*;

public class DataReader {

    public static List<GraphGenerator.DataPoint> readCryptoHistory(String crypto, int hours) {
        List<GraphGenerator.DataPoint> list = new ArrayList<>();

        try {
            List<String> cryptos = List.of(crypto);
            List<Crypto> records = CryptoRepository.findByNamesAndRecentHours(cryptos, hours);

            for (Crypto c : records) {
                list.add(new GraphGenerator.DataPoint(c.timestamp.toLocalDateTime(), c.price));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static Map<String, List<GraphGenerator.DataPoint>> readMultipleCryptoHistory(List<String> cryptos,
            int hours) {
        Map<String, List<GraphGenerator.DataPoint>> result = new HashMap<>();

        try {
            List<Crypto> records = CryptoRepository.findByNamesAndRecentHours(cryptos, hours);

            for (String name : cryptos)
                result.put(name, new ArrayList<>());

            for (Crypto c : records)
                result.get(c.name).add(new GraphGenerator.DataPoint(c.timestamp.toLocalDateTime(), c.price));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}
