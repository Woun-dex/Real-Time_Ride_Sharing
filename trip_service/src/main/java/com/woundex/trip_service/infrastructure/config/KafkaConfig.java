package com.woundex.trip_service.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic tripRequestedTopic() {
        return TopicBuilder.name("trip.requested")
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic tripStartedTopic() {
        return TopicBuilder.name("trip.started")
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic tripCompletedTopic() {
        return TopicBuilder.name("trip.completed")
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic tripCancelledTopic() {
        return TopicBuilder.name("trip.cancelled")
            .partitions(3)
            .replicas(1)
            .build();
    }
}