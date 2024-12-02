package com._INFINI.PI.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

@Service
public class AnalyseService {

    private static final String API_KEY = "ct2b8nhr01qiurr3qr80ct2b8nhr01qiurr3qr8g";
    private static final String BASE_URL = "https://finnhub.io/api/v1";
    private static final String API_KEY_ALPHA = "I6VW80LJSG57TYG4";
    private static final String FINNHUB_BASIC_FINANCIALS_URL =
            "https://finnhub.io/api/v1/stock/metric?symbol={symbol}&metric=all&token=" + API_KEY;


    public Map<String, Object> getQuote(String symbol) {
        String url = BASE_URL + "/quote?symbol=" + symbol + "&token=" + API_KEY;
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            if (response == null || response.isEmpty()) {
                throw new RuntimeException("Empty response from quote API");
            }

            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.toMap();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch quote for symbol: " + symbol, e);
        }
    }

    public Map<String, Object> getRecommendationTrends(String symbol) {
        String url = BASE_URL + "/stock/recommendation?symbol=" + symbol + "&token=" + API_KEY;
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            if (response == null || response.isEmpty()) {
                return Collections.emptyMap();
            }

            JSONArray jsonArray = new JSONArray(response);
            return jsonArray.length() > 0 ? jsonArray.getJSONObject(0).toMap() : Collections.emptyMap();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch recommendation trends for symbol: " + symbol, e);
        }
    }

    public String getHistoricalOptions(String symbol) {
        try {
            String url = "https://www.alphavantage.co/query?function=HISTORICAL_OPTIONS&symbol=" + symbol + "&apikey=" + API_KEY_ALPHA;
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            // Parse and validate response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            // Check for API limitation message
            if (root.has("Information")) {
                throw new RuntimeException("API Usage Limit: " + root.get("Information").asText());
            }

            // Your parsing logic here
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch historical options for: " + symbol, e);
        }
    }


    // Fonction de calcul du risque d'action
    public double calculateStockRisk(String historicalData) {
        try {
            JSONObject response = new JSONObject(historicalData);
            JSONArray data = response.getJSONArray("data");

            double totalImpliedVolatility = 0;
            double totalGamma = 0;

            for (int i = 0; i < data.length(); i++) {
                JSONObject option = data.getJSONObject(i);
                totalImpliedVolatility += option.optDouble("implied_volatility", 0);
                totalGamma += option.optDouble("gamma", 0);
            }

            int optionCount = data.length();
            double avgImpliedVolatility = totalImpliedVolatility / optionCount;
            double avgGamma = totalGamma / optionCount;

            return avgImpliedVolatility * (1 + avgGamma);

        } catch (Exception e) {
            return -1; // Return -1 in case of an error
        }
    }

    public Map<String, Object> getCompanyBasicFinancialsAndSave(String symbol) {
        String url = FINNHUB_BASIC_FINANCIALS_URL.replace("{symbol}", symbol);
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            if (response == null || response.isEmpty()) {
                throw new RuntimeException("Empty response from financials API");
            }

            JSONObject jsonResponse = new JSONObject(response);
            Map<String, Object> financialData = jsonResponse.toMap();
            saveFinancialsToJson(financialData, symbol);

            return financialData;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch and save financials for symbol: " + symbol, e);
        }
    }

    private void saveFinancialsToJson(Map<String, Object> financialData, String symbol) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("src/main/resources/static/data/" + symbol + "_financials.json");
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            mapper.writeValue(file, financialData);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save financial data to JSON for symbol: " + symbol, e);
        }
    }
    public String getNewsSentiment(String symbol) {
        try {
            String url = "https://www.alphavantage.co/query?function=NEWS_SENTIMENT&tickers=" + symbol + "&apikey=" + API_KEY_ALPHA;
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            // Parse and validate response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            // Check for API limitation or error messages
            if (root.has("Information")) {
                throw new RuntimeException("API Usage Limit: " + root.get("Information").asText());
            }

            if (root.has("Error Message")) {
                throw new RuntimeException("Error from API: " + root.get("Error Message").asText());
            }

            // Process and return sentiment data
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch news sentiment for: " + symbol, e);
        }
    }

}
