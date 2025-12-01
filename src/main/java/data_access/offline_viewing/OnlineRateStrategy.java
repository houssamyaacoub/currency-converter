package data_access.offline_viewing;

import data_access.RateCache;
import entity.OfflineRate;
import org.json.JSONObject;

import java.net.http.*;
import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class OnlineRateStrategy implements OfflineRateStrategy {

    @Override
    public OfflineRate loadRates() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.exchangerate.host/latest"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject json = new JSONObject(response.body());
        JSONObject jsonRates = json.getJSONObject("rates");

        Map<String, Double> rates = new HashMap<>();
        for (String key : jsonRates.keySet()) {
            rates.put(key, jsonRates.getDouble(key));
        }

        Instant now = Instant.now();
        RateCache.save(rates, now);

        return new OfflineRate(rates, now);
    }
}
