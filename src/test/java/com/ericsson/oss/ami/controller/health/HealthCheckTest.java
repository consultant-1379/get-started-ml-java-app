/*******************************************************************************
 * COPYRIGHT Ericsson 2021
 *
 *
 *
 * The copyright to the computer program(s) herein is the property of
 *
 * Ericsson Inc. The programs may be used and/or copied only with written
 *
 * permission from Ericsson Inc. or in accordance with the terms and
 *
 * conditions stipulated in the agreement/contract under which the
 *
 * program(s) have been supplied.
 ******************************************************************************/

package com.ericsson.oss.ami.controller.health;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ericsson.oss.ami.configuration.KafkaConfig;
import com.ericsson.oss.ami.controller.kafka.KafkaInputListener;
import com.ericsson.oss.ami.controller.kafka.KafkaOutputController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.ericsson.oss.ami.CoreApplication;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {CoreApplication.class, HealthCheck.class})
public class HealthCheckTest {

    @MockBean
    private KafkaInputListener kafkaInputListener;

    @MockBean
    private KafkaOutputController kafkaOutputController;

    @MockBean
    private KafkaConfig kafkaConfig;

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mvc;
    @Autowired
    private HealthCheck health;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void get_health_status_ok() throws Exception {
        mvc.perform(get("/actuator/health").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().json("{'status' : 'UP'}"));
    }

    @Test
    public void get_health_status_not_ok() throws Exception {
        health.failHealthCheck("HC down");
        mvc.perform(get("/actuator/health").contentType(MediaType.APPLICATION_JSON)).andExpect(status().is5xxServerError())
                .andExpect(content().json("{'status' : 'DOWN'}"));
    }
}
