/*
 * Copyright (c) 2017 EMC Corporation All Rights Reserved
 */

package com.sgene.ng.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Component
class NeuralNetworkAPI {

    private final static Logger LOGGER = LoggerFactory.getLogger(NeuralNetworkAPI.class);

    private static class ErrorHandler implements ResponseErrorHandler {

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            LOGGER.warn("Neural network responded with: " + response.getStatusCode());
        }

        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return !response.getStatusCode().is2xxSuccessful();
        }
    }

    private final RestTemplate restTemplate;

    @Autowired
    public NeuralNetworkAPI(RestTemplateBuilder restTemplateBuilder, @Value("${neural.network.root.uri}") String neuralNetworkRootURI) {
        this.restTemplate = restTemplateBuilder.rootUri(neuralNetworkRootURI).build();
        this.restTemplate.setErrorHandler(new ErrorHandler());
    }

    Map<String, Double> predict(String image) {
        ParameterizedTypeReference<Map<String, Double>> type = new ParameterizedTypeReference<Map<String, Double>>() {};
        return this.restTemplate.exchange("/predict", HttpMethod.POST, new HttpEntity<>(image), type).getBody();
    }

    String fit(String image, String label) {
        return this.restTemplate.postForEntity("/fit?label=" + label, image, String.class).getBody();
    }

    String[] labels() {
        return this.restTemplate.getForEntity("/labels", String[].class).getBody();
    }
}
