package com.woundex.ws_driver_service.messaging.kafka;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DriverAvailabilityProducerTest {

    @Test
    void publish_sends_event_to_driver_availability_topic() {
        KafkaTemplate<String, Object> kafka = mock(KafkaTemplate.class);
        DriverAvailabilityProducer producer = new DriverAvailabilityProducer(kafka);

        Object event = new Object();
        producer.publish(event);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafka).send(topicCaptor.capture(), payloadCaptor.capture());

        assertEquals("driver-availability", topicCaptor.getValue());
        assertSame(event, payloadCaptor.getValue());
    }
}
