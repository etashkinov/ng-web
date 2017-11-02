package com.sgene.ng.web;

import com.sgene.ng.web.service.Confidence;
import com.sgene.ng.web.service.Prediction;
import com.sgene.ng.web.service.PredictionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RestResourceControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private PredictionService service;

    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    public void shouldReturnLabels() throws Exception {
        Map<String, String> option = new HashMap<>();
        option.put("label", "Line");
        option.put("value", "line");
        when(service.getLabels()).thenReturn(Collections.singletonList(option));

        mvc.perform(get("/api/getLabels"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("[{\"label\":\"Line\",\"value\":\"line\"}]"));
    }

    @Test
    public void shouldFitImage() throws Exception {
        when(service.fit("image", "line")).thenReturn("success");

        mvc.perform(post("/api/fit?label=line").content("base64,image"))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));
    }

    @Test
    public void shouldPredict() throws Exception {
        Prediction prediction = new Prediction(Confidence.UNCERTAIN, Arrays.asList("line", "circle"));
        when(service.predict("image")).thenReturn(prediction);

        mvc.perform(post("/api/predict").content("base64,image"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"confidence\":\"UNCERTAIN\", \"labels\":[\"line\",\"circle\"]}"));
    }
}