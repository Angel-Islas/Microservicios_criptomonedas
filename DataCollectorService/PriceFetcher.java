import java.net.*;
import java.io.*;
import org.json.*;

public class PriceFetcher {

    public static JSONObject fetchPrices() throws IOException {
        URL url = new URL("https://api.coingecko.com/api/v3/simple/price?ids=bitcoin,ethereum,cardano,solana,binancecoin,polkadot,dogecoin,ripple,tron,litecoin&vs_currencies=usd");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line, response = "";
        while ((line = reader.readLine()) != null) {
            response += line;
        }
        reader.close();

        return new JSONObject(response);
    }
}
