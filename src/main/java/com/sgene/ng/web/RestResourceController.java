/*
 * Copyright (c) 2017 EMC Corporation All Rights Reserved
 */

package com.sgene.ng.web;

import com.sgene.ng.web.service.Prediction;
import com.sgene.ng.web.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api/")
public class RestResourceController {

    private final PredictionService service;

    @Autowired
    RestResourceController(PredictionService service) {
        this.service = service;
    }

    @RequestMapping(value = "predict", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    Prediction predict(@RequestBody String encodedImage) throws IOException {
        return service.predict(getImageInBase64(encodedImage));
    }

    private String getImageInBase64(String encodedImage) throws IOException {
        return encodedImage.split(",")[1];
    }

    @RequestMapping(value = "fit", method = RequestMethod.POST)
    String fit(@RequestBody String encodedImage, @RequestParam String label) throws IOException {
        String image = getImageInBase64(encodedImage);
        return service.fit(image, label);
    }

    @RequestMapping(value = "getLabels", produces = MediaType.APPLICATION_JSON_VALUE)
    Collection<Map<String, String>> getLabels() {
        return service.getLabels();
    }
}
