import java.sql.*;

public class DatabaseConnector {
    private static final String DB_HOST = System.getenv().getOrDefault("DB_HOST", "postgres_db");
    private static final String DB_URL = "jdbc:postgresql://" + DB_HOST + ":5432/crypto_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "postgres";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se pudo cargar el driver de PostgreSQL", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
