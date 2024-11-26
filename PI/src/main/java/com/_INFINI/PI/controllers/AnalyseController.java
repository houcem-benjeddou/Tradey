package com._INFINI.PI.controllers;

import com._INFINI.PI.services.AnalyseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public String getHistoricalOptions(@RequestParam String symbol) {
        return analyseService.getHistoricalOptions(symbol);
    }


    @GetMapping("/risk")
    public String calculateRisk(@RequestParam String symbol) {
        // Fetch historical options data
        String historicalData = analyseService.getHistoricalOptions(symbol);

        // Calculate the risk
        double riskScore = analyseService.calculateStockRisk(historicalData);

        // Return the risk score
        return "Risk Score for " + symbol + ": " + riskScore;
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
