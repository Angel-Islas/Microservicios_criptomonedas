import java.net.*;
import java.io.*;
import org.json.*;

public class PriceFetcher {
    private static final String API_BASE = "https://api.coingecko.com/api/v3/simple/price";
    private static final String CURRENCY = "usd";
    private static final String[] CRYPTO_IDS = {
            "bitcoin", "ethereum", "ripple", "litecoin", "cardano",
            "polkadot", "chainlink", "stellar", "dogecoin", "uniswap"
    };

    public static JSONObject fetchPrices() throws IOException {
        /*
         * Realiza una solicitud a la API de CoinGecko para obtener los precios
         * de las criptomonedas especificadas en CRYPTO_IDS.
         * La respuesta se simplifica para incluir solo el precio en USD
         * para cada criptomoneda.
         * 
         * @param: No recibe parámetros.
         * 
         * @return: Un objeto JSON que contiene los precios de las criptomonedas
         * en formato {"nombre": precio_en_usd}.
         * 
         * @throws IOException: Si hay un error al realizar la solicitud HTTP
         * o al procesar la respuesta.
         */
        URL url = new URL(API_BASE + "?ids=" + String.join(",", CRYPTO_IDS) + "&vs_currencies=" + CURRENCY);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Error en la conexión: HTTP " + responseCode);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder rawResponse = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                rawResponse.append(line);
            }

            JSONObject rawData = new JSONObject(rawResponse.toString());
            JSONObject simplified = new JSONObject();

            for (String key : rawData.keySet()) {
                JSONObject crypto = rawData.getJSONObject(key);
                simplified.put(key, crypto.getDouble("usd"));
            }

            return simplified;
        } finally {
            conn.disconnect();
        }
    }
}
