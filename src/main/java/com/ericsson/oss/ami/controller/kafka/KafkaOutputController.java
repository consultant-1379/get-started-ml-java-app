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

import com.ericsson.oss.ami.configuration.KafkaConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Data
@Component
public class KafkaOutputController {

    @Autowired
    KafkaConfig kafkaConfig;

    @Autowired
    private KafkaTemplate kafkaOutputTemplate;

    @Value("${spring.kafka.topic.output.name}")
    private String outputTopicName;

    private final List<Message<String>> messagesToSendToOutput = Collections.synchronizedList(new ArrayList<>());

    public KafkaOutputController(final KafkaTemplate kafkaOutputTemplate) {
        super();
        this.kafkaOutputTemplate = kafkaOutputTemplate;
    }

    public void sendKafkaMessages() {
        int count = 0;
        for (final Message<String> message : messagesToSendToOutput){
            try {
                kafkaOutputTemplate.send(message);
                log.info("SENT message: '{}' to '{}' Topic", message, outputTopicName);
                count++;
            }catch (final Exception exception) {
                log.error("FAILED to send message: '{}' to '{}' Topic.", message, outputTopicName);
                throw exception;
            }
        }
        log.info("SENT {}/{} messages to Topic: '{}'", count, messagesToSendToOutput.size(), outputTopicName);
    }

    public void addToKafkaMessages(final String message) {

        final Message<String> msg = MessageBuilder.withPayload(message)
                .setHeader(KafkaHeaders.TOPIC, outputTopicName).build();
        log.debug("Adding message: '{}' to '{}' Topic", message, outputTopicName);
        messagesToSendToOutput.add(msg);
    }

}
