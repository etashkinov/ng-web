/*
 * Copyright (c) 2017 EMC Corporation All Rights Reserved
 */

package com.sgene.gn.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NeuralNetworkAPI {
    private final RestTemplate restTemplate;

    @Autowired
    public NeuralNetworkAPI(RestTemplateBuilder restTemplateBuilder, @Value("${neural.network.root.uri}") String neuralNetworkRootURI) {
        this.restTemplate = restTemplateBuilder.rootUri(neuralNetworkRootURI).build();
    }

    public String evaluate(byte[] image) {
        return this.restTemplate.postForEntity("/evaluate", image, String.class).getBody();
    }
}
