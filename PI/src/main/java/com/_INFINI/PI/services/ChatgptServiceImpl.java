package com._INFINI.PI.services;

import com._INFINI.PI.openai.Answer;
import com._INFINI.PI.openai.Call;
import com._INFINI.PI.openai.OutputDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;



@Slf4j
@Service

public class ChatgptServiceImpl implements IChatgptService {
    @Value("${openai.apikey}")
    private String openaiApiKey;
    private ObjectMapper jsonMapper;
    @Value("${openai.api.url}")
    private String URL;
    @Value("${openai.model}")
    private String model;
    @Value("${openai.maxTokens}")
    private Integer max_tokens;
    @Value("${openai.temperature}")
    private Double temperature;
    private final HttpClient client = HttpClient.newHttpClient();
    public ChatgptServiceImpl(ObjectMapper jsonMapper){
        this.jsonMapper=jsonMapper;
    }

    @Override
    public String sendChatgptRequest(String body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openaiApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }


    @Override
    public OutputDto sendPrompt(String prompt) throws Exception {
        Call call = new Call(model,prompt,max_tokens,temperature);
        String responseBody = sendChatgptRequest(jsonMapper.writeValueAsString(call));
        System.out.println(responseBody);
        Answer answer = jsonMapper.readValue(responseBody, Answer.class);

        String text;
        if (answer.getChoices() != null && !answer.getChoices().isEmpty()) {
            text = answer.getChoices().get(0).getText();
        } else {
            text = "No choices available.";
        }

        OutputDto outputDto= new OutputDto(prompt, text);
        return outputDto;
    }







}
