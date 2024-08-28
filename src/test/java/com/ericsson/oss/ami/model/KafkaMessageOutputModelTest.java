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

package com.ericsson.oss.ami.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KafkaMessageOutputModelTest {

    @Test
    public void testNoArgsConstructor() {
        KafkaMessageOutputModel model = new KafkaMessageOutputModel();
        assertNotNull(model);
    }

    @Test
    public void testAllArgsConstructor() {
        KafkaMessageOutputModel model = new KafkaMessageOutputModel("performance");
        assertNotNull(model);
        assertEquals("performance", model.getPerformanceMetric());
    }

    @Test
    public void testGettersAndSetters() {
        KafkaMessageOutputModel model = new KafkaMessageOutputModel();
        model.setPerformanceMetric("performance");

        assertEquals("performance", model.getPerformanceMetric());
    }

    @Test
    public void testBuilder() {
        KafkaMessageOutputModel model = KafkaMessageOutputModel.builder()
                .performanceMetric("performance")
                .build();

        assertNotNull(model);
        assertEquals("performance", model.getPerformanceMetric());
    }
}