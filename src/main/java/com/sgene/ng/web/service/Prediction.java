/*
 * Copyright (c) 2017 EMC Corporation All Rights Reserved
 */

package com.sgene.ng.web.service;

import java.util.Collections;
import java.util.List;

public class Prediction {
    private final Confidence confidence;
    private final List<String> labels;

    public Prediction(Confidence confidence, List<String> labels) {
        this.confidence = confidence;
        this.labels = labels;
    }

    public Confidence getConfidence() {
        return confidence;
    }

    public List<String> getLabels() {
        return Collections.unmodifiableList(labels);
    }
}
