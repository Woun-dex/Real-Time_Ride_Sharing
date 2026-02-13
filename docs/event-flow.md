# Event Flow – Ride Sharing Platform

> How events propagate through Kafka topics, from trigger to consumer action.

---

## Topic Registry

| Kafka Topic               | Payload Type          | Producer            | Consumer(s)                        |
|----------------------------|-----------------------|---------------------|------------------------------------|
| `trip.requested`           | `TripRequestedEvent`  | Trip Service        | Matching Service                   |
| `trip.matched`             | `TripMatchedEvent`    | Matching Service    | Notification Service               |
| `trip.accepted`            | `TripAcceptedEvent`   | Trip Service        | Notification Service               |
| `trip.status.changed`      | `StatusChangedEvent`  | Trip Service        | Notification Service, Analytics    |
| `driver.location.updated`  | `LocationEvent`       | Location Service    | Trip Service (ETA), Matching       |
| `driver.status.changed`    | `DriverStatusEvent`   | User / Driver Svc   | Matching Service                   |
| `payment.requested`        | `PaymentRequestEvent` | Trip Service        | Payment Service                    |
| `payment.processed`        | `PaymentEvent`        | Payment Service     | Notification Service, Trip Service |
| `rating.created`           | `RatingEvent`         | Trip Service        | User Service (update avg rating)   |

---

## Detailed Event Flows

### 1. Trip Request → Driver Assignment

```
Rider App                                                      Driver App
   │                                                              ▲
   │  POST /api/trips/request                                     │ WebSocket push
   ▼                                                              │
┌──────────────┐  ── trip.requested ──▶  ┌──────────────────┐     │
│ Trip Service │                         │ Matching Service  │     │
│ (REQUESTED)  │                         │ find nearby driver│     │
└──────────────┘                         └────────┬─────────┘     │
                                                  │               │
                                    query Location Service        │
                                    (drivers within 5 km)         │
                                                  │               │
                                         ── driver.requested ──▶  │
                                                                  │
                                         ┌────────────────────┐   │
                                         │ Notification Svc   │───┘
                                         │ (push to driver WS)│
                                         └────────────────────┘
```

### 2. Driver Accepts → Rider Notified

```
Driver App                                                     Rider App
   │                                                              ▲
   │  POST /api/trips/{id}/accept                                 │ WebSocket push
   ▼                                                              │
┌──────────────┐  ── trip.accepted ──▶  ┌────────────────────┐    │
│ Trip Service │                        │ Notification Svc   │────┘
│ (ACCEPTED)   │                        │ (push to rider WS) │
└──────────────┘                        └────────────────────┘
```

### 3. Status Transitions (Event-Driven)

Each transition publishes `trip.status.changed`:

```
REQUESTED ──▶ MATCHED ──▶ ACCEPTED ──▶ DRIVER_EN_ROUTE ──▶ ARRIVED
     │                                                        │
 (timeout)                                               IN_PROGRESS
     │                                                        │
  CANCELLED                                              COMPLETED
                                                              │
                                                    ── payment.requested ──▶ Payment Service
                                                                                    │
                                                    ◀── payment.processed ──────────┘
                                                              │
                                                     PAYMENT_PROCESSED
```

### 4. Location Streaming

```
Driver App ──ws──▶ Gateway ──route──▶ Location Service ──▶ Redis (latest pos)
                                            │
                                    ── driver.location.updated ──▶ Trip Service (recalc ETA)
                                                                        │
                                                             ── trip.status.changed (ETA) ──▶ WS → Rider
```

---

## Event Envelope (JSON Schema)

```json
{
  "eventId":   "string (UUID)",
  "eventType": "TRIP_REQUESTED | TRIP_MATCHED | TRIP_ACCEPTED | STATUS_CHANGED | LOCATION_UPDATED | PAYMENT_PROCESSED | RATING_CREATED",
  "tripId":    "string",
  "riderId":   "string | null",
  "driverId":  "string | null",
  "pickup":    { "latitude": 0.0, "longitude": 0.0, "address": "string" },
  "dropoff":   { "latitude": 0.0, "longitude": 0.0, "address": "string" },
  "fare":      0.0,
  "status":    "string",
  "timestamp": "ISO-8601"
}
```

---

## Retry & Dead-Letter Strategy

| Scenario               | Retry Policy               | Dead-Letter Topic            |
|------------------------|----------------------------|------------------------------|
| Consumer processing error | 3 retries, exponential backoff (1 s, 2 s, 4 s) | `<topic>.dlq`  |
| Serialisation failure  | No retry                    | `<topic>.dlq`                |
| Timeout (no ACK)       | Re-deliver after 30 s       | After 5 attempts → DLQ       |

---

## Idempotency

All consumers must be **idempotent**. The `eventId` (UUID) is used to deduplicate:

```java
if (processedEventIds.contains(event.eventId())) {
    log.info("Duplicate event {} – skipping", event.eventId());
    return;
}
processedEventIds.add(event.eventId());
```

For production, store `eventId` in a database/Redis set with a TTL of 24 h.
