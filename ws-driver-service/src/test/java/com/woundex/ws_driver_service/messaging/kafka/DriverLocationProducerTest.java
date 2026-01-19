package com.woundex.ws_driver_service.messaging.kafka;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DriverLocationProducerTest {

    @Test
    void publish_sends_event_to_driver_locations_topic() {
        KafkaTemplate<String, Object> kafka = mock(KafkaTemplate.class);
        DriverLocationProducer producer = new DriverLocationProducer(kafka);

        Object event = new Object();
        producer.publish(event);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafka).send(topicCaptor.capture(), payloadCaptor.capture());

        assertEquals("driver-locations", topicCaptor.getValue());
        assertSame(event, payloadCaptor.getValue());
    }
}
