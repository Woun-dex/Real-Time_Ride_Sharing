package com.woundex.matching_service.matching;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.woundex.matching_service.domain.event.DriverAcceptedEvent;
import com.woundex.matching_service.domain.event.DriverAssignedEvent;
import com.woundex.matching_service.domain.event.DriverExpiredEvent;
import com.woundex.matching_service.domain.event.DriverOfferEvent;
import com.woundex.matching_service.domain.event.EventPublisher;
import com.woundex.matching_service.domain.event.Topics;
import com.woundex.matching_service.domain.event.TripRequestedEvent;
import com.woundex.matching_service.domain.model.NearbyDriver;
import com.woundex.matching_service.domain.model.Position;
import com.woundex.matching_service.infra.redis.RedisGeoService;

@Service
public class MatchingService {

    private static final Logger log = LoggerFactory.getLogger(MatchingService.class);
    private static final long OFFER_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(2);

    private final RedisGeoService geo;
    private final EventPublisher publisher;
    private final PendingOfferStore pendingStore;

    public MatchingService(RedisGeoService geo, EventPublisher publisher, PendingOfferStore pendingStore) {
        this.geo = geo;
        this.publisher = publisher;
        this.pendingStore = pendingStore;
    }

    // ── Step 1: trip.requested consumed ──────────────────────────────────────

    public void handleTripRequested(TripRequestedEvent event) {
        Set<String> excluded = new HashSet<>();
        offerToNextDriver(event, excluded);
    }

    // ── Step 2: driver.accepted consumed ─────────────────────────────────────

    public void handleDriverAccepted(DriverAcceptedEvent event) {
        PendingOffer pending = pendingStore.remove(event.getTripId());

        if (pending == null) {
            log.warn("No pending offer for trip {} — already expired or assigned", event.getTripId());
            return;
        }

        if (pending.isExpired()) {
            log.warn("Offer for trip {} expired before driver {} accepted",
                    event.getTripId(), event.getDriverId());
            // retry with next driver
            offerToNextDriver(pending.getTripEvent(), pending.getTriedDriverIds());
            return;
        }

        if (!pending.getDriverId().equals(event.getDriverId())) {
            log.warn("Driver {} tried to accept trip {} but offer was for driver {}",
                    event.getDriverId(), event.getTripId(), pending.getDriverId());
            pendingStore.put(event.getTripId(), pending);
            return;
        }

        // ✅ Valid acceptance → emit driver.assigned
        DriverAssignedEvent assigned = new DriverAssignedEvent(
                event.getTripId(),
                event.getDriverId(),
                pending.getTripEvent().getRiderId(),
                pending.getDriver().getPosition().getLat(),
                pending.getDriver().getPosition().getLon());

        publisher.publish(Topics.DRIVER_ASSIGNED, event.getTripId(), assigned);
        log.info("✅ Driver {} assigned to trip {}", event.getDriverId(), event.getTripId());
    }

    // ── Step 3: scheduled timeout check ──────────────────────────────────────

    @Scheduled(fixedRate = 10_000)
    public void expireOffers() {
        pendingStore.getAll().forEach((tripId, offer) -> {
            if (offer.isExpired()) {
                pendingStore.remove(tripId);

                // notify that this driver's offer expired
                publisher.publish(Topics.DRIVER_OFFER_EXPIRED, tripId,
                        new DriverExpiredEvent(tripId, offer.getDriverId(), "TIMEOUT"));
                log.info("⏰ Offer expired for trip {} driver {}", tripId, offer.getDriverId());

                // try next driver
                offerToNextDriver(offer.getTripEvent(), offer.getTriedDriverIds());
            }
        });
    }

    // ── Internal: pick next untried driver and send offer ────────────────────

    private void offerToNextDriver(TripRequestedEvent event, Set<String> triedDriverIds) {
        double radius = event.getRadiusMeters() > 0 ? event.getRadiusMeters() : 1000;
        int limit = event.getLimit() > 0 ? event.getLimit() : 50;

        List<NearbyDriver> drivers = geo.findNearby(
                new Position(event.getPickupLat(), event.getPickupLon()), radius, limit);

        // find first driver not already tried
        Optional<NearbyDriver> candidate = drivers.stream()
                .filter(d -> !triedDriverIds.contains(d.getDriverId()))
                .findFirst();

        if (candidate.isEmpty()) {
            log.warn("❌ No more drivers available for trip {}", event.getTripId());
            publisher.publish(Topics.NO_DRIVER_AVAILABLE, event.getTripId(),
                    new DriverExpiredEvent(event.getTripId(), null, "NO_DRIVERS_LEFT"));
            return;
        }

        NearbyDriver chosen = candidate.get();
        long expiresAt = System.currentTimeMillis() + OFFER_TIMEOUT_MS;

        // track this driver as tried
        Set<String> updatedTried = new HashSet<>(triedDriverIds);
        updatedTried.add(chosen.getDriverId());

        PendingOffer pending = new PendingOffer(
                event.getTripId(), chosen.getDriverId(), event, chosen, expiresAt, updatedTried);
        pendingStore.put(event.getTripId(), pending);

        // emit offer → ws-driver service pushes to driver's WebSocket
        DriverOfferEvent offer = new DriverOfferEvent(
                event.getTripId(),
                chosen.getDriverId(),
                event.getRiderId(),
                event.getPickupLat(), event.getPickupLon(),
                event.getDropoffLat(), event.getDropoffLon(),
                expiresAt);

        publisher.publish(Topics.DRIVER_OFFER, event.getTripId(), offer);
        log.info("📤 Sent offer to driver {} for trip {} (expires {})",
                chosen.getDriverId(), event.getTripId(), expiresAt);
    }
}