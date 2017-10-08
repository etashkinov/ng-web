/*
 * Copyright (c) 2017 EMC Corporation All Rights Reserved
 */

package com.sgene.gn.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/")
public class RestResourceController {

    private final NeuralNetworkAPI api;

    @Autowired RestResourceController(NeuralNetworkAPI api) {
        this.api = api;
    }

    @RequestMapping(value="evaluate", method = RequestMethod.POST)
    String evaluate(@RequestBody byte[] image) {
        System.err.println("Evaluate image:");
        System.err.println(Arrays.toString(image));
        try {
            return this.api.evaluate(image);
        } catch (Exception e) {
            e.printStackTrace();
            return String.valueOf(image.length);
        }
    }

    @RequestMapping(value = "categories", produces = MediaType.APPLICATION_JSON_VALUE)
    Collection<Map<String,Object>> geCategories() {
        Map<String, Object> emp1 = new HashMap<>();
        emp1.put("value", 0);
        emp1.put("label", "Line");

        Map<String, Object> emp2 = new HashMap<>();
        emp2.put("value", 1);
        emp2.put("label", "Circle");

        Map<String, Object> emp3 = new HashMap<>();
        emp3.put("value", 2);
        emp3.put("label", "Arch");

        return Arrays.asList(emp1, emp2, emp3);
    }
}
