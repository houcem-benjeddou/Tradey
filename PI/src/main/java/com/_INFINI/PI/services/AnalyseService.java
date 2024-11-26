package com._INFINI.PI.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
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
    private final String API_KEY = "ct2b8nhr01qiurr3qr80ct2b8nhr01qiurr3qr8g";
    private final String BASE_URL = "https://finnhub.io/api/v1";
    private final String API_KEY_alpha = "I6VW80LJSG57TYG4";
    private  final String FINNHUB_BASIC_FINANCIALS_URL = "https://finnhub.io/api/v1/stock/metric?symbol={symbol}&metric=all&token=" + API_KEY;

    public Map<String, Object> getQuote(String symbol) {
        String url = BASE_URL + "/quote?symbol=" + symbol + "&token=" + API_KEY;
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            if (response == null || response.isEmpty()) {
                throw new RuntimeException("Empty response from quote API");
            }

            // Convert JSON response to a Map for flexibility
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.toMap();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch quote for symbol: " + symbol, e);
        }
    }

    public Map<String, Object> getRecommendationTrends(String symbol) {
        String url = BASE_URL + "/stock/recommendation?symbol=" + symbol + "&token=" + API_KEY;
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            if (response == null || response.isEmpty()) {
                throw new RuntimeException("Empty response from recommendation API");
            }

            // Convert JSON response to a Map for easy manipulation
            JSONArray jsonArray = new JSONArray(response);
            return jsonArray.length() > 0 ? jsonArray.getJSONObject(0).toMap() : Collections.emptyMap();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch recommendation trends for symbol: " + symbol, e);
        }
    }

    public String getHistoricalOptions(String symbol) {
        try {
            // Construct the API URL
            String url = "https://www.alphavantage.co/query?function=HISTORICAL_OPTIONS&symbol="
                    + symbol + "&apikey=" + API_KEY_alpha;

            // Initialize the HTTP connection
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            // Check the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Return the JSON response
                return response.toString();
            } else {
                return "GET request failed. HTTP Code: " + responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    /**
     * Calculate the risk of a stock based on its historical options data.
     *
     * @param historicalData JSON response from the historical options API.
     * @return The calculated risk score.
     */
    public double calculateStockRisk(String historicalData) {
        try {
            // Parse the JSON response
            JSONObject response = new JSONObject(historicalData);
            JSONArray data = response.getJSONArray("data");

            // Initialize risk metrics
            double totalImpliedVolatility = 0;
            double totalGamma = 0;

            // Loop through each option contract
            for (int i = 0; i < data.length(); i++) {
                JSONObject option = data.getJSONObject(i);

                // Extract implied volatility and gamma
                double impliedVolatility = option.optDouble("implied_volatility", 0);
                double gamma = option.optDouble("gamma", 0);

                // Accumulate metrics
                totalImpliedVolatility += impliedVolatility;
                totalGamma += gamma;
            }

            // Calculate averages
            int optionCount = data.length();
            double avgImpliedVolatility = totalImpliedVolatility / optionCount;
            double avgGamma = totalGamma / optionCount;

            // Define a simple risk score formula
            double riskScore = avgImpliedVolatility * (1 + avgGamma);

            // Return the calculated risk score
            return riskScore;

        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Return -1 in case of an error
        }
    }

    public String analyzeStockSentiment(String symbol) {
        try {
            // Étape 1 : Récupérer les données sentimentales via Finnhub
            String apiUrl = BASE_URL + "/news-sentiment?symbol=" + symbol + "&token=" + API_KEY;
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "Failed to fetch sentiment data. HTTP Code: " + responseCode;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Étape 2 : Convertir la réponse JSON en un objet exploitable
            JSONObject sentimentData = new JSONObject(response.toString());
            String sentimentJson = sentimentData.toString(4); // JSON formaté pour affichage

            // Étape 3 : Extraire et analyser les éléments pertinents
            double positiveScore = sentimentData.optDouble("positiveScore", 0.0);
            double negativeScore = sentimentData.optDouble("negativeScore", 0.0);
            double neutralScore = sentimentData.optDouble("neutralScore", 0.0);

            // Appliquer une logique personnalisée d'interprétation
            String sentimentAnalysis;
            if (positiveScore > negativeScore && positiveScore > neutralScore) {
                sentimentAnalysis = "Overall sentiment is positive with a score of " + positiveScore;
            } else if (negativeScore > positiveScore && negativeScore > neutralScore) {
                sentimentAnalysis = "Overall sentiment is negative with a score of " + negativeScore;
            } else {
                sentimentAnalysis = "Overall sentiment is neutral with a score of " + neutralScore;
            }

            // Étape 4 : Retourner une réponse consolidée (brut JSON + analyse)
            return "Sentiment Analysis for " + symbol + ":\n" +
                    sentimentAnalysis + "\n\nRaw Sentiment Data:\n" + sentimentJson;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error during sentiment analysis: " + e.getMessage();
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


    //
public Map<String, Object> getCompanyBasicFinancialsAndSave(String symbol) {
    RestTemplate restTemplate = new RestTemplate();
    String url = FINNHUB_BASIC_FINANCIALS_URL.replace("{symbol}", symbol);
    String response = restTemplate.getForObject(url, String.class);

    // Convert the JSON response to a Map
    JSONObject jsonResponse = new JSONObject(response);
    Map<String, Object> financialData = jsonResponse.toMap();

    // Save the financial data to a JSON file
    saveFinancialsToJson(financialData, symbol);

    // Return the financial data as a Map
    return financialData;
}

    // Method to save financial data to JSON file
    private void saveFinancialsToJson(Map<String, Object> financialData, String symbol) {
        ObjectMapper mapper = new ObjectMapper();
        // The file will be named with the symbol to track different companies
        File file = new File("src/main/resources/static/data/" + symbol + "_financials.json");
        try {
            // Create the directory if necessary
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            // Write the data to the JSON file
            mapper.writeValue(file, financialData);
            System.out.println("Financial data saved to JSON for symbol: " + symbol);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
