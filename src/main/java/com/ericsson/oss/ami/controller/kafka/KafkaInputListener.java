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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class KafkaInputListener {

    @Value("${spring.kafka.topic.output.name}")
    private String topicName;

    private String jsonPayload;

    private final List<String> messageList = new ArrayList<>();

    @KafkaListener(
            containerFactory = "concurrentKafkaListenerContainerFactory",
            topics = "${spring.kafka.topic.input.name}",
            concurrency = "1",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(String message){

        synchronized (this) {
            messageList.add(message);

            StringBuilder jsonPayloadBuilder = new StringBuilder("{\"data\":{\"ndarray\":[");

            log.debug("Received message: {}", message);

            log.debug("Starting to process the consumed records ...");

            for (int i = 0; i < messageList.size(); i++) {
                jsonPayloadBuilder.append("[\"").append(messageList.get(i)).append("\"]");

                if (i < messageList.size() - 1) {
                    jsonPayloadBuilder.append(",");
                }
            }
            jsonPayloadBuilder.append("]}}");
            jsonPayload = jsonPayloadBuilder.toString();
        }

        log.info("Received {} records from topic: {}", messageList.size(), topicName);
        log.info("Constructed JSON payload: {}", jsonPayload);

        //TODO: implement to call modelinferenceservice with UserAuth or TokenAuth
        //TODO: call the kafkaOutputController.addToKafkaMessages() and sendKafkaMessages() to send the prediction to a topic
    }

    //For testing purposes only
    String getJsonPayload(){
        return jsonPayload;
    }
}
