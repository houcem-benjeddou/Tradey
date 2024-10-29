package com._INFINI.PI.services;

import com._INFINI.PI.openai.OutputDto;

import java.io.IOException;

public interface IChatgptService {

    String sendChatgptRequest(String body) throws IOException, InterruptedException;


    OutputDto sendPrompt(String prompt) throws Exception;
}
