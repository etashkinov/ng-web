/*
 * Copyright (c) 2017 EMC Corporation All Rights Reserved
 */

package com.sgene.gn.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/evaluate")
public class MainController {

    private final NeuralNetworkAPI api;

    @Autowired
    MainController(NeuralNetworkAPI api) {
        this.api = api;
    }

    @RequestMapping(method = RequestMethod.POST)
    String evaluate(@RequestBody byte[] image) {
        return this.api.evaluate(image);
    }
}
