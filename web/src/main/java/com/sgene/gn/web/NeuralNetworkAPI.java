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
class NeuralNetworkAPI {
    private final RestTemplate restTemplate;

    @Autowired
    public NeuralNetworkAPI(RestTemplateBuilder restTemplateBuilder, @Value("${neural.network.root.uri}") String neuralNetworkRootURI) {
        this.restTemplate = restTemplateBuilder.rootUri(neuralNetworkRootURI).build();
    }

    String predict(String image) {
        return this.restTemplate.postForEntity("/predict", image, String.class).getBody();
    }

    String fit(String image, String label) {
        return this.restTemplate.postForEntity("/fit?label=" + label, image, String.class).getBody();
    }

    String[] labels() {
        return this.restTemplate.getForEntity("/labels", String[].class).getBody();
    }
}
