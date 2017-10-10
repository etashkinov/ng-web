/*
 * Copyright (c) 2017 EMC Corporation All Rights Reserved
 */

package com.sgene.ng.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/")
public class RestResourceController {
    private final static Logger LOGGER = LoggerFactory.getLogger(RestResourceController.class);

    private final NeuralNetworkAPI api;

    @Autowired RestResourceController(NeuralNetworkAPI api) {
        this.api = api;
    }

    @RequestMapping(value = "predict", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    Prediction predict(@RequestBody String encodedImage) throws IOException {
        String image = getImageInBase64(encodedImage);
        Map<String, Double> predict = api.predict(image);
        return getPrediction(predict);
    }

    private Prediction getPrediction(Map<String, Double> predict) {
        List<Entry<String, Double>> sorted = predict.entrySet().stream()
                .sorted(Comparator.comparing(e -> -e.getValue()))
                .collect(Collectors.toList());

        Entry<String, Double> best = sorted.get(0);
        if (best.getValue() >= Confidence.SURE.getThreshold()) {
            return new Prediction(Confidence.SURE, Collections.singleton(capitalise(best.getKey())));
        } else if (best.getValue() >= Confidence.UNCERTAIN.getThreshold()) {
            return new Prediction(Confidence.UNCERTAIN, Arrays.asList(
                    capitalise(best.getKey()),
                    capitalise(sorted.get(1).getKey())));
        } else {
            return new Prediction(Confidence.DONT_KNOW, Collections.emptyList());
        }
    }

    private String getImageInBase64(String encodedImage) throws IOException {
        return encodedImage.split(",")[1];
    }

    @RequestMapping(value = "fit", method = RequestMethod.POST)
    String fit(@RequestBody String encodedImage, @RequestParam String label) throws IOException {
        String image = getImageInBase64(encodedImage);
        try {
            return api.fit(image, label);
        } catch (Exception e) {
            LOGGER.error("Failed to fit " + label + " with " + encodedImage, e);
            return String.valueOf(image.length());
        }
    }

    @RequestMapping(value = "labels", produces = MediaType.APPLICATION_JSON_VALUE)
    Collection<Map<String, Object>> getLabels() {
        String[] labels = api.labels();
        Collection<Map<String, Object>> result = new ArrayList<>(labels.length);
        for (String label : labels) {
            Map<String, Object> option = new HashMap<>();
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
