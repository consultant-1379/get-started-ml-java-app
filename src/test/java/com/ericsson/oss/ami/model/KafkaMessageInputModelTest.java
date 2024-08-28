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

class KafkaMessageInputModelTest {

    @Test
    public void testNoArgsConstructor() {
        KafkaMessageInputModel model = new KafkaMessageInputModel();
        assertNotNull(model);
    }

    @Test
    public void testAllArgsConstructor() {
        KafkaMessageInputModel model = new KafkaMessageInputModel("load", "strength", "temp", "humidity");
        assertNotNull(model);
        assertEquals("load", model.getNetworkLoad());
        assertEquals("strength", model.getSignalStrength());
        assertEquals("temp", model.getTemperature());
        assertEquals("humidity", model.getHumidity());
    }

    @Test
    public void testGettersAndSetters() {
        KafkaMessageInputModel model = new KafkaMessageInputModel();
        model.setNetworkLoad("load");
        model.setSignalStrength("strength");
        model.setTemperature("temp");
        model.setHumidity("humidity");

        assertEquals("load", model.getNetworkLoad());
        assertEquals("strength", model.getSignalStrength());
        assertEquals("temp", model.getTemperature());
        assertEquals("humidity", model.getHumidity());
    }

    @Test
    public void testBuilder() {
        KafkaMessageInputModel model = KafkaMessageInputModel.builder()
                .networkLoad("load")
                .signalStrength("strength")
                .temperature("temp")
                .humidity("humidity")
                .build();

        assertNotNull(model);
        assertEquals("load", model.getNetworkLoad());
        assertEquals("strength", model.getSignalStrength());
        assertEquals("temp", model.getTemperature());
        assertEquals("humidity", model.getHumidity());
    }

}