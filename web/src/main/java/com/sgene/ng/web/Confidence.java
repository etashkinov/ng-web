/*
 * Copyright (c) 2017 EMC Corporation All Rights Reserved
 */

package com.sgene.ng.web;

public enum Confidence {
    SURE(0.6),
    UNCERTAIN(0.3),
    DONT_KNOW(0);

    private final double threshold;

    Confidence(double threshold) {
        this.threshold = threshold;
    }

    public double getThreshold() {
        return threshold;
    }
}
