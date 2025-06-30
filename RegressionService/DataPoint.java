import java.time.LocalDateTime;

public class DataPoint {
    public LocalDateTime timestamp;
    public double price;

    public DataPoint(LocalDateTime timestamp, double price) {
        this.timestamp = timestamp;
        this.price = price;
    }
}
