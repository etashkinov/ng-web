/*
 * Copyright (c) 2017 EMC Corporation All Rights Reserved
 */

package com.sgene.ng.web;

import java.util.Collection;

public class Prediction {
    private final Confidence confidence;
    private final Collection<String> labels;

    public Prediction(Confidence confidence, Collection<String> labels) {
        this.confidence = confidence;
        this.labels = labels;
    }

    public Confidence getConfidence() {
        return confidence;
    }

    public Collection<String> getLabels() {
        return labels;
    }
}
