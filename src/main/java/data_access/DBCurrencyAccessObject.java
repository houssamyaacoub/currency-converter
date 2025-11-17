package data_access;
import org.json.JSONObject;
import use_case.convert.ConvertDataAccessInterface;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DBCurrencyAccessObject implements ConvertDataAccessInterface {

    private static final String API_KEY = "API_KEY";

    private static final String API_URL_TEMPLATE =
            "http://api.exchangeratesapi.io/v1/convert?access_key=%s&from=%s&to=%s&amount=%f";

    @Override
    public ApiConversionResult getConversion(String from, String to, double amount) {
        String apiUrl = String.format(API_URL_TEMPLATE, API_KEY, from, to, amount);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("API request failed. Status code: " + response.statusCode());
            }

            return parseApiResponse(response.body());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to parse API response: " + e.getMessage(), e);
        }
    }

    private ApiConversionResult parseApiResponse(String jsonResponse) {
        try {
            JSONObject responseJson = new JSONObject(jsonResponse);

            if (!responseJson.optBoolean("success", false)) {
                String errorInfo = responseJson.optJSONObject("error").optString("info", "Unknown API Error");
                throw new RuntimeException("API Error: " + errorInfo);
            }

            double rate = responseJson.getJSONObject("info").getDouble("rate");
            double convertedAmount = responseJson.getDouble("result");
            String timestamp = responseJson.getString("date");

            return new ApiConversionResult(rate, convertedAmount, timestamp);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse API response: " + e.getMessage(), e);
        }
    }
}