package com.sgene.ng.web.service;

import com.sgene.ng.web.remote.NeuralNetworkAPI;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PredictionServiceTest {


    private PredictionService service;
    private NeuralNetworkAPI api;

    @Before
    public void setup() {
        api = mock(NeuralNetworkAPI.class);
        service = new PredictionService(api);
    }

    @Test
    public void shouldReturnLabels() throws Exception {
        when(api.getLabels()).thenReturn(new String[] {"line"});

        List<Map<String, String>> result = service.getLabels();
        assertEquals(1, result.size());
        assertEquals("line", result.get(0).get("value"));
        assertEquals("Line", result.get(0).get("label"));
    }

    @Test
    public void shouldFitImage() throws Exception {
        when(api.fit("image", "line")).thenReturn("success");

        String result = service.fit("image", "line");

        assertEquals("success", result);
    }


    @Test
    public void shouldPredict() throws Exception {
        Map<String, Double> confidence = new HashMap<>();
        confidence.put("arch", 0.05);
        confidence.put("line", 0.9);
        confidence.put("circle", 0.05);
        when(api.predict("image")).thenReturn(confidence);

        Prediction prediction = service.predict("image");
        assertEquals(Confidence.SURE, prediction.getConfidence());
        assertEquals(1, prediction.getLabels().size());
        assertEquals("Line", prediction.getLabels().get(0));
    }

    @Test
    public void shouldPredictForSure() throws Exception {
        Map<String, Double> confidence = new HashMap<>();
        confidence.put("arch", 0.05);
        confidence.put("line", 0.9);
        confidence.put("circle", 0.05);

        Prediction prediction = service.getPrediction(confidence);
        assertEquals(Confidence.SURE, prediction.getConfidence());
        assertEquals(1, prediction.getLabels().size());
        assertEquals("Line", prediction.getLabels().get(0));
    }


    @Test
    public void shouldPredictForUncertain() throws Exception {
        Map<String, Double> confidence = new HashMap<>();
        confidence.put("arch", 0.45);
        confidence.put("line", 0.5);
        confidence.put("circle", 0.05);

        Prediction prediction = service.getPrediction(confidence);
        assertEquals(Confidence.UNCERTAIN, prediction.getConfidence());
        assertEquals(2, prediction.getLabels().size());
        assertEquals("Line", prediction.getLabels().get(0));
        assertEquals("Arch", prediction.getLabels().get(1));
    }

    @Test
    public void shouldPredictForDontKnow() throws Exception {
        Map<String, Double> confidence = new HashMap<>();
        confidence.put("arch", 0.33);
        confidence.put("line", 0.33);
        confidence.put("circle", 0.34);

        Prediction prediction = service.getPrediction(confidence);
        assertEquals(Confidence.DONT_KNOW, prediction.getConfidence());
        assertEquals(0, prediction.getLabels().size());
    }
}