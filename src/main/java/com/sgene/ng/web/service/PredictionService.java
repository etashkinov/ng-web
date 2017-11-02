package com.sgene.ng.web.service;

import com.sgene.ng.web.remote.NeuralNetworkAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PredictionService {
    private final static Logger LOGGER = LoggerFactory.getLogger(PredictionService.class);

    private final NeuralNetworkAPI api;

    @Autowired
    PredictionService(NeuralNetworkAPI api) {
        this.api = api;
    }

    public Prediction predict(String image) throws IOException {
        Map<String, Double> predict = api.predict(image);
        return getPrediction(predict);
    }

    Prediction getPrediction(Map<String, Double> predict) {
        List<Map.Entry<String, Double>> sorted = predict.entrySet().stream()
                .sorted(Comparator.comparing(e -> -e.getValue()))
                .collect(Collectors.toList());

        Map.Entry<String, Double> best = sorted.get(0);
        if (best.getValue() >= Confidence.SURE.getThreshold()) {
            return new Prediction(Confidence.SURE, Collections.singletonList(capitalise(best.getKey())));
        } else if (best.getValue() >= Confidence.UNCERTAIN.getThreshold()) {
            return new Prediction(Confidence.UNCERTAIN, Arrays.asList(
                    capitalise(best.getKey()),
                    capitalise(sorted.get(1).getKey())));
        } else {
            return new Prediction(Confidence.DONT_KNOW, Collections.emptyList());
        }
    }

    public String fit(String image, String label) throws IOException {
        try {
            return api.fit(image, label);
        } catch (Exception e) {
            LOGGER.error("Failed to fit " + label + " with " + image, e);
            return String.valueOf(image.length());
        }
    }

    public List<Map<String, String>> getLabels() {
        String[] labels = api.getLabels();
        List<Map<String, String>> result = new ArrayList<>(labels.length);
        for (String label : labels) {
            Map<String, String> option = new HashMap<>();
            option.put("value", label);
            option.put("label", capitalise(label));
            result.add(option);
        }

        return result;
    }

    private String capitalise(String label) {
        return label.substring(0, 1).toUpperCase() + label.substring(1);
    }
}
