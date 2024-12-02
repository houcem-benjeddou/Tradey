package com._INFINI.PI.controllers;

import com._INFINI.PI.services.AnalyseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/analyse")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AnalyseController {

    @Autowired
    private AnalyseService analyseService;
/////////analyse technique
    @GetMapping("/quote")
    public Map<String, Object> getStockQuote(@RequestParam String symbol) {
        return analyseService.getQuote(symbol);
    }

    // Endpoint pour obtenir les tendances de recommandations
    @GetMapping("/recommendation")
    public Map<String, Object> getRecommendationTrends(@RequestParam String symbol) {
        return analyseService.getRecommendationTrends(symbol);
    }
    // Endpoint to fetch historical options data
    @GetMapping("/historical-options")
    public ResponseEntity<?> getHistoricalOptions(@RequestParam String symbol) {
        try {
            // Wrap the response in ResponseEntity
            String response = analyseService.getHistoricalOptions(symbol);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Endpoint pour calculer l'évaluation du risque
    @GetMapping("/risk")
    public ResponseEntity<Map<String, Object>> calculateRisk(@RequestParam String symbol) {
        // Récupérer les données historiques des options
        String historicalData = analyseService.getHistoricalOptions(symbol);

        // Calculer le score de risque
        double riskScore = analyseService.calculateStockRisk(historicalData);

        // Préparer une réponse JSON structurée
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("date", LocalDate.now().toString()); // Ajouter une date actuelle
        response.put("value", riskScore); // Score calculé

        // Retourner la réponse au format JSON
        return ResponseEntity.ok(response);
    }
    //////analyse fondamentale
    @GetMapping("/api/basic-financials")
    public Map<String, Object> getBasicFinancials(@RequestParam String symbol) {
        return analyseService.getCompanyBasicFinancialsAndSave(symbol);
    }
    // Endpoint combiné pour l'analyse sentimentale complète
    @GetMapping("/sentiment-full")
    public String getFullSentimentAnalysis(@RequestParam String symbol) {
        return analyseService.analyzeStockSentiment(symbol);
    }
    // Endpoint pour l'analyse sentimentale via Finnhub
    @GetMapping("/sentiment")
    public String getSentimentAnalysis(@RequestParam String symbol) {
        return analyseService.getSentimentAnalysis(symbol);
    }

    // Endpoint pour l'analyse personnalisée d'un texte
    @PostMapping("/sentiment-custom")
    public String performSentimentAnalysis(@RequestBody String text) {
        return analyseService.performSentimentAnalysis(text);
    }
}
