package com._INFINI.PI.openai;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class Call {
    private String model;
    private String prompt;
    private Integer max_tokens;
    private Double temperature;
}