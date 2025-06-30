import java.sql.Timestamp;

public class Crypto {
    public int id;
    public String name;
    public double price;
    public Timestamp timestamp;

    public Crypto(String name, double price, Timestamp timestamp) {
        this.name = name;
        this.price = price;
        this.timestamp = timestamp;
    }

    public Crypto(int id, String name, double price, Timestamp timestamp) {
        this(name, price, timestamp);
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("%s: %.2f USD @ %s", name, price, timestamp.toString());
    }
}
