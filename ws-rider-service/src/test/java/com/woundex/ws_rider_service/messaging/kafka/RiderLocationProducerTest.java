package com.woundex.ws_rider_service.messaging.kafka;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RiderLocationProducerTest {

    @Test
    void publish_sends_event_to_rider_locations_topic() {
        KafkaTemplate<String, Object> kafka = mock(KafkaTemplate.class);
        RiderLocationProducer producer = new RiderLocationProducer(kafka);

        Object event = new Object();
        producer.publish(event);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafka).send(topicCaptor.capture(), payloadCaptor.capture());
        assertEquals("rider-locations", topicCaptor.getValue());
        assertSame(event, payloadCaptor.getValue());
    }
}
