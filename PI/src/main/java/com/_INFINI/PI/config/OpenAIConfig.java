package com._INFINI.PI.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAIConfig {

    @Value("${openai.apikey}")
    private String openaiApiKey;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Créer un intercepteur pour ajouter les headers nécessaires à chaque requête
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openaiApiKey);
            // S'assurer que le Content-Type est défini sur application/json
            request.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            return execution.execute(request, body);
        };

        // Ajouter l'intercepteur configuré au RestTemplate
        restTemplate.getInterceptors().add(interceptor);

        return restTemplate;
    }
}