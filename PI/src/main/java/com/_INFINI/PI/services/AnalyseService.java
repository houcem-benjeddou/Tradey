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

    public AnalyseService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

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

    private final RestTemplate restTemplate;




    public String analyzeStockSentiment(String symbol) {
        try {
            String apiUrl = "https://finnhub.io/api/v1/news-sentiment?symbol=" + symbol + "&token=" + API_KEY;

            // Ajouter l'en-tête Authorization si nécessaire
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + API_KEY); // ou selon ce qui est requis par Finnhub
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Faire la requête GET avec les en-têtes appropriés
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

            // Traitement de la réponse comme d'habitude
            if (response.getStatusCode().is2xxSuccessful()) {
                // Code d'analyse de la réponse
            } else {
                return "Failed to fetch sentiment data. HTTP Code: " + response.getStatusCodeValue();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return "Error during sentiment analysis: " + e.getMessage();
        } catch (Exception e) {
            return "An unexpected error occurred: " + e.getMessage();
        }
        return symbol;
    }

    private String getSentimentAnalysis(double positiveScore, double negativeScore, double neutralScore) {
        if (positiveScore > negativeScore && positiveScore > neutralScore) {
            return "Overall sentiment is positive with a score of " + positiveScore;
        } else if (negativeScore > positiveScore && negativeScore > neutralScore) {
            return "Overall sentiment is negative with a score of " + negativeScore;
        } else {
            return "Overall sentiment is neutral with a score of " + neutralScore;
        }
    }


    public String getSentimentAnalysis(String symbol) {
        try {
            // Construire l'URL de l'API pour récupérer l'analyse sentimentale
            String url = BASE_URL + "/news-sentiment?symbol=" + symbol + "&token=" + API_KEY;

            // Initialiser une connexion HTTP
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Vérifier la réponse de l'API
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Lire la réponse
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Retourner la réponse JSON brute des données sentimentales
                return response.toString();
            } else {
                return "GET request failed. HTTP Code: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public String performSentimentAnalysis(String text) {
        try {
            // Implémentez ici la logique NLP. Remplacez par une librairie réelle si nécessaire.
            // Par exemple : utilisez TextBlob (Python), Stanford CoreNLP (Java), etc.
            double sentimentScore = analyzeTextWithNLP(text); // Méthode fictive pour illustrer l'extraction de score.

            // Interprétez le score pour déterminer le sentiment
            if (sentimentScore > 0) {
                return "Positive sentiment with score: " + sentimentScore;
            } else if (sentimentScore < 0) {
                return "Negative sentiment with score: " + sentimentScore;
            } else {
                return "Neutral sentiment.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error in sentiment analysis: " + e.getMessage();
        }
    }

    // Exemple fictif de méthode d'analyse de texte avec un score
    private double analyzeTextWithNLP(String text) {
        // Remplacez avec une bibliothèque NLP. Ici, un score aléatoire pour démonstration.
        return text.contains("good") ? 1.0 : text.contains("bad") ? -1.0 : 0.0;
    }
}
