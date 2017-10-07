/*
 * Copyright (c) 2017 EMC Corporation All Rights Reserved
 */

package com.sgene.gn.web;

import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(value = "employees")
    Collection<Map<String,String>> getEmployees() {
        Map<String, String> emp1 = new HashMap<>();
        emp1.put("firstName", "Homer");
        emp1.put("lastName", "Simpson");
        emp1.put("description", "Security Officer");

        Map<String, String> emp2 = new HashMap<>();
        emp2.put("firstName", "Montgomery");
        emp2.put("lastName", "Burns");
        emp2.put("description", "Owner");

        Map<String, String> emp3 = new HashMap<>();
        emp3.put("firstName", "Waylon");
        emp3.put("lastName", "Smithers");
        emp3.put("description", "CEO");

        return Arrays.asList(emp1, emp2, emp3);
    }
}
