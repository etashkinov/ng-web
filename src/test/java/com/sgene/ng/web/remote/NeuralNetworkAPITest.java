package com.sgene.ng.web.remote;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringJUnit4ClassRunner.class)
@RestClientTest(NeuralNetworkAPI.class)
public class NeuralNetworkAPITest {

    private static final double DELTA = 0.00001;

    @Autowired
    private NeuralNetworkAPI api;

    @Autowired
    private MockRestServiceServer mockServer;

    @Test
    public void shouldGetLabels() throws Exception {
        mockServer.expect(requestTo("/labels"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(new ClassPathResource("labels.json"), MediaType.APPLICATION_JSON));

        String[] labels = api.getLabels();

        mockServer.verify();
        assertNotNull(labels);
        assertEquals(3, labels.length);
        assertEquals("arch", labels[1]);
    }

    @Test
    public void shouldGetPredictions() throws Exception {
        /*
         * {
         *  "line": 0.453453,
         *  "arch": 0.0001,
         *  "circle": 0.463554
         * }
         */
        mockServer.expect(requestTo("/predict"))
                .andExpect(content().string("image"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(new ClassPathResource("predict.json"), MediaType.APPLICATION_JSON));

        Map<String, Double> prediction = api.predict("image");

        mockServer.verify();
        assertEquals(3, prediction.size());
        assertEquals(0.453453, prediction.get("line"), DELTA);
        assertEquals(0.0001, prediction.get("arch"), DELTA);
        assertEquals(0.463554, prediction.get("circle"), DELTA);
    }

    @Test
    public void shouldFit() throws Exception {
        String expectedResult = "success";
        mockServer.expect(requestTo("/fit?label=line"))
                .andExpect(content().string("image"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(expectedResult, MediaType.TEXT_PLAIN));

        String result = api.fit("image", "line");

        mockServer.verify();
        assertEquals(expectedResult, result);
    }
}