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

package com.ericsson.oss.ami.configuration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka
@ActiveProfiles("test")
class KafkaConfigTest {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServerConfig;
    @Autowired
    private KafkaConfig kafkaConfig;

    @Test
    void kafkaConsumerFactory() {
        final Map<String, Object> expectedConfiguration = consumerConfig();
        final Map<String, Object> actualConfiguration = kafkaConfig.kafkaConsumerFactory().getConfigurationProperties();
        assertEquals(expectedConfiguration.size(), actualConfiguration.size());
        assertEquals(expectedConfiguration, actualConfiguration, "Consumer configuration does not contain expected values");
    }

    @Test
    void kafkaProducerFactory() {
        final Map<String, Object> expectedConfiguration = producerConfig();
        final Map<String, Object> actualConfiguration = kafkaConfig.kafkaProducerFactory().getConfigurationProperties();
        assertEquals(expectedConfiguration.size(), actualConfiguration.size());
        assertEquals(expectedConfiguration, actualConfiguration, "Producer configuration does not contain expected values");
    }

    @Test
    public void concurrentKafkaListenerContainerFactory() {
        DefaultKafkaConsumerFactory<String, String> consumerFactory = Mockito.mock(DefaultKafkaConsumerFactory.class);
        when(consumerFactory.getConfigurationProperties()).thenReturn(kafkaConfig.kafkaConsumerFactory().getConfigurationProperties());
        ConcurrentKafkaListenerContainerFactory<String, String> factory = kafkaConfig.concurrentKafkaListenerContainerFactory();
        assertEquals(consumerFactory.getConfigurationProperties(), factory.getConsumerFactory().getConfigurationProperties());
    }

    @Test
    void kafkaOutputTemplate() {
        final KafkaTemplate<String, Object> kafkaTemplate = kafkaConfig.kafkaOutputTemplate();
        assertFalse(kafkaTemplate == null, "KafkaOutputTemplate should not be null");
    }


    private Map<String, Object> producerConfig(){
        final Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerConfig);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
        return config;
    }

    public Map<String, Object> consumerConfig() {
        final Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerConfig);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, false);
        return props;
    }
}
