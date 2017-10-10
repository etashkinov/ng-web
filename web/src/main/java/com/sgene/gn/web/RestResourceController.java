/*
 * Copyright (c) 2017 EMC Corporation All Rights Reserved
 */

package com.sgene.gn.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/")
public class RestResourceController {
    private final static Logger LOGGER = LoggerFactory.getLogger(RestResourceController.class);

    private final NeuralNetworkAPI api;

    @Autowired RestResourceController(NeuralNetworkAPI api) {
        this.api = api;
    }

    @RequestMapping(value="predict", method = RequestMethod.POST)
    String predict(@RequestBody String encodedImage) throws IOException {
        String image = getImageInBase64(encodedImage);
        try {
            return api.predict(image);
        } catch (Exception e) {
            LOGGER.error("Failed to predict " + encodedImage, e);
            return  String.valueOf(image.length());
        }
    }

    private String getImageInBase64(String encodedImage) throws IOException {
        return encodedImage.split(",")[1];
    }

    @RequestMapping(value="fit", method = RequestMethod.POST)
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
    Collection<Map<String,Object>> getLabels() {
        String[] labels = api.labels();
        Collection<Map<String, Object>> result = new ArrayList<>(labels.length);
        for (int i = 0; i < labels.length; i++) {
            Map<String, Object> option = new HashMap<>();
            option.put("value", labels[i]);
            option.put("label", labels[i].substring(0, 1).toUpperCase() + labels[i].substring(1));
            result.add(option);
        }

        return result;
    }
}
