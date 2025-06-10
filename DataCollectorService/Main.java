import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {
        Timer timer = new Timer();
        System.out.println("DataCollectorService iniciado...");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    var prices = PriceFetcher.fetchPrices();
                    DatabaseManager.save(prices);
                    System.out.println("Precios actualizados.");
                } catch (Exception e) {
                    System.out.println("Error al obtener precios: " + e.getMessage());
                }
            }
        }, 0, 60000); // cada minuto
    }
}
