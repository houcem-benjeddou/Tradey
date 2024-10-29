package com._INFINI.PI.controllers;

import com._INFINI.PI.entities.ChatGPTRequest;
import com._INFINI.PI.entities.ChatGptResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/bot")
@CrossOrigin("*")
public class CustomBotController {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/chat")
    public String chat(@RequestParam("prompt") String prompt) {
        // Création de l'objet de requête avec le modèle et le prompt
        ChatGPTRequest request = new ChatGPTRequest(model, prompt);

        // Création des headers de la requête HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Création de l'entité HttpEntity qui encapsule l'objet de requête et les headers
        HttpEntity<ChatGPTRequest> entity = new HttpEntity<>(request, headers);

        // Envoi de la requête POST et réception de la réponse
        ChatGptResponse chatGptResponse = restTemplate.postForObject(apiURL, entity, ChatGptResponse.class);

        // Retourner le contenu de la réponse
        return chatGptResponse != null && chatGptResponse.getChoices() != null && !chatGptResponse.getChoices().isEmpty()
                ? chatGptResponse.getChoices().get(0).getMessage().getContent()
                : "No response";
}
}
