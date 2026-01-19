package com.woundex.ws_driver_service.config;

import com.woundex.ws_driver_service.application.handler.DriverAvailabilityHandler;
import com.woundex.ws_driver_service.application.handler.DriverGpsMessageHandler;
import com.woundex.ws_driver_service.application.handler.TripEventHandler;
import com.woundex.ws_driver_service.application.port.DriverStateStore;
import com.woundex.ws_driver_service.application.port.EventPublisher;
import com.woundex.ws_driver_service.application.port.PushNotifier;
import com.woundex.ws_driver_service.messaging.kafka.DriverAvailabilityProducer;
import com.woundex.ws_driver_service.messaging.kafka.DriverLocationProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class ApplicationConfig {

    @Bean
    public DriverGpsMessageHandler driverGpsMessageHandler(DriverLocationProducer producer) {
        return new DriverGpsMessageHandler(producer);
    }

    @Bean
    public DriverAvailabilityHandler driverAvailabilityHandler(
            DriverStateStore stateStore,
            DriverAvailabilityProducer producer
    ) {
        return new DriverAvailabilityHandler(stateStore, producer);
    }

    @Bean
    public TripEventHandler tripEventHandler(PushNotifier pushNotifier) {
        return new TripEventHandler(pushNotifier);
    }
}
