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

package com.ericsson.oss.ami.controller.kafka;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@EmbeddedKafka(partitions = 3, topics = "testTopicKafka")
class KafkaOutputControllerTest {

    @Autowired
    KafkaOutputController kafkaOutputController;

    @Autowired
    private KafkaTemplate kafkaOutputTemplate;

    @Test
    void messageForKafkaOutputHasValidPayloadTest(){
        final String message = "testMessage";

        kafkaOutputController.addToKafkaMessages(message);

        final List<Message<String>> messages = (List<Message<String>>) ReflectionTestUtils.getField(kafkaOutputController, "messagesToSendToOutput");

        assertEquals("testMessage", messages.get(0).getPayload());
    }

    @Test
    void sendKafkaMessagesTest(){
        String message = "testMessage";
        KafkaTemplate kafkaTemplate = mock(KafkaTemplate.class);
        KafkaOutputController kafkaOutputController = new KafkaOutputController(kafkaTemplate);
        kafkaOutputController.addToKafkaMessages(message);
        kafkaOutputController.sendKafkaMessages();
        verify(kafkaTemplate).send((Message<?>) Mockito.any());
    }
}

