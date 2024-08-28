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

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.atLeastOnce;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@EmbeddedKafka(topics = "eric-oss-get-started-ml-java-app-topic-in")
@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@ActiveProfiles("test")
class KafkaInputListenerTest {

    @Value("${spring.kafka.topic.input.name}")
    private String topicName;

    @SpyBean
    private KafkaInputListener kafkaInputListener;

    private Producer<String, String> producer;

    @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Captor
    ArgumentCaptor<String> messageCaptor;

    @BeforeEach
    void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        configs.put("value.serializer", StringSerializer.class);
        producer =
                new DefaultKafkaProducerFactory<>(
                        configs, new StringSerializer(), new StringSerializer())
                        .createProducer();
    }

    @AfterEach
    void shutdown() {
        producer.close();
    }


    @Test
    public void testListen() throws InterruptedException {
        String record1 = "120,75,15,50";
        String record2 = "200,63,42,88";
        producer.send(new ProducerRecord<>(topicName, record1));
        producer.send(new ProducerRecord<>(topicName, record2));
        await()
                .atLeast(Duration.of(100L, ChronoUnit.MILLIS))
                .atMost(Duration.of(3_000L, ChronoUnit.MILLIS))
                        .untilAsserted(
                                () -> {
                                    Mockito.verify(kafkaInputListener, atLeastOnce()).listen(messageCaptor.capture());
                                }
                        );
        String capturedMessage = messageCaptor.getValue();
        assertNotNull(capturedMessage);
        String jsonPayload = kafkaInputListener.getJsonPayload();
        String expectedJson = "{\"data\":{\"ndarray\":[[\"120,75,15,50\"],[\"200,63,42,88\"]]}}";
        assertEquals(expectedJson, jsonPayload);

    }
}