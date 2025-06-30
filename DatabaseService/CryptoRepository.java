import java.sql.*;
import java.util.*;

public class CryptoRepository {

    public static void create(Crypto crypto) throws SQLException {
        String sql = "INSERT INTO cryptos (name, price, timestamp) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, crypto.name);
            stmt.setDouble(2, crypto.price);
            stmt.setTimestamp(3, crypto.timestamp);
            stmt.executeUpdate();
        }
    }

    public static List<Crypto> findAll() throws SQLException {
        List<Crypto> list = new ArrayList<>();
        String sql = "SELECT * FROM cryptos";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Crypto(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getTimestamp("timestamp")));
            }
        }
        return list;
    }

    public static Crypto findByName(String name) throws SQLException {
        String sql = "SELECT * FROM cryptos WHERE name = ?";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Crypto(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getTimestamp("timestamp"));
                }
            }
        }
        return null;
    }

    public static List<Crypto> findByPriceRange(double minPrice, double maxPrice) throws SQLException {
        List<Crypto> list = new ArrayList<>();
        String sql = "SELECT * FROM cryptos WHERE price BETWEEN ? AND ?";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, minPrice);
            stmt.setDouble(2, maxPrice);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Crypto(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getTimestamp("timestamp")));
                }
            }
        }
        return list;
    }

    public static List<Crypto> findByTimestamp(Timestamp timestamp) throws SQLException {
        List<Crypto> list = new ArrayList<>();
        String sql = "SELECT * FROM cryptos WHERE timestamp = ?";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, timestamp);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Crypto(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getTimestamp("timestamp")));
                }
            }
        }
        return list;
    }

    public static List<Crypto> findLatestSnapshot() throws SQLException {
        String sql = """
                    SELECT * FROM cryptos WHERE timestamp = (
                        SELECT MAX(timestamp) FROM cryptos
                    )
                """;
        List<Crypto> list = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Crypto(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getTimestamp("timestamp")));
            }
        }
        return list;
    }

    public static List<Crypto> findByNamesAndRecentHours(List<String> names, int hours) throws SQLException {
        if (names == null || names.isEmpty())
            return Collections.emptyList();

        String placeholders = String.join(",", Collections.nCopies(names.size(), "?"));
        String sql = "SELECT * FROM cryptos WHERE name IN (" + placeholders
                + ") AND timestamp >= NOW() - INTERVAL '? hours' ORDER BY timestamp";

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            int i = 1;
            for (String name : names) {
                stmt.setString(i++, name);
            }
            stmt.setInt(i, hours);

            List<Crypto> list = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Crypto(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getTimestamp("timestamp")));
                }
            }
            return list;
        }
    }

}
