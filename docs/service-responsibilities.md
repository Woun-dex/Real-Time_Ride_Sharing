# Service Responsibilities – Ride Sharing Platform

> Single-responsibility breakdown for every micro-service in the MVP.

---

## 1. Gateway Service

| Attribute       | Detail |
|-----------------|--------|
| **Module**      | `gateway-service` |
| **Framework**   | Spring Cloud Gateway (reactive / Netty) |
| **Port**        | 8080 |

### Responsibilities
- Single entry point for **all** HTTP and WebSocket traffic
- Route requests to downstream services via Eureka service discovery
- Rate limiting (Redis-backed `RequestRateLimiter`)
- Circuit breaking (Resilience4j) with fallback endpoints
- CORS configuration for mobile and web clients
- JWT token forwarding / validation at the edge
- WebSocket upgrade proxying (`/ws/**` → WS-Driver / WS-Rider services)
- Request/response logging (`LoggingFilter`)

### Key Config
- `GatewayRouteConfig` – programmatic route definitions
- `RateLimitConfig` – per-user token-bucket
- `CorsConfig` – allowed origins
- `WebSocketHeaderFilter` – injects auth headers into WS upgrade

---

## 2. User Service

| Attribute       | Detail |
|-----------------|--------|
| **Module**      | `user_service` |
| **Framework**   | Spring Boot (Web MVC) |
| **Database**    | PostgreSQL |

### Responsibilities
- User registration (Rider & Driver roles)
- Authentication (JWT issue & refresh)
- Profile CRUD (name, phone, e-mail, avatar)
- Driver on-boarding (vehicle info, licence verification)
- Password hashing (BCrypt)
- Role-based access control
- Maintain average driver/rider ratings

### API Endpoints
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/users/register` | Register new user |
| POST | `/api/auth/login` | Authenticate |
| GET | `/api/users/me` | Get own profile |
| PUT | `/api/users/me` | Update profile |
| DELETE | `/api/users/me` | Soft-delete account |
| PUT | `/api/drivers/status` | Toggle ONLINE / OFFLINE |

### Events Published
| Topic | When |
|-------|------|
| `driver.status.changed` | Driver goes ONLINE / OFFLINE |

---

## 3. Trip Service

| Attribute       | Detail |
|-----------------|--------|
| **Module**      | `trip_service` |
| **Framework**   | Spring Boot (Web MVC) |
| **Database**    | PostgreSQL |
| **Pattern**     | CQRS (Command / Query separation) |

### Responsibilities
- Create trip requests (pickup, dropoff, ride type)
- Estimated fare calculation (distance × rate + surge)
- Trip status state machine (REQUESTED → … → COMPLETED)
- Driver assignment (on accept)
- Trip history queries
- Rating & review storage
- Publish events on every status transition

### API Endpoints
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/trips/request` | Request a new trip |
| POST | `/api/trips/{id}/accept` | Driver accepts |
| PUT | `/api/trips/{id}/status` | Transition status |
| GET | `/api/trips/{id}` | Get trip details |
| GET | `/api/trips/history` | Rider/driver history |
| POST | `/api/trips/{id}/rate` | Submit rating |
| POST | `/api/trips/{id}/cancel` | Cancel trip |

### Events Published
| Topic | When |
|-------|------|
| `trip.requested` | New trip created |
| `trip.accepted` | Driver accepts |
| `trip.status.changed` | Any status transition |
| `payment.requested` | Trip completed |

### Events Consumed
| Topic | Action |
|-------|--------|
| `payment.processed` | Mark trip as PAYMENT_PROCESSED |
| `driver.location.updated` | Recalculate ETA |

---

## 4. Location Service

| Attribute       | Detail |
|-----------------|--------|
| **Module**      | `location_service` |
| **Framework**   | Spring Boot (WebFlux – reactive) |
| **Data Store**  | Redis (geo-index) |

### Responsibilities
- Receive real-time driver GPS coordinates (via WebSocket or REST)
- Store latest position in Redis using `GEOADD`
- Geo-spatial queries: "find drivers within *N* km of point"
- Publish location updates to Kafka for downstream consumers
- Driver proximity detection (within 50 m → trigger ARRIVED)

### API Endpoints
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/location/update` | Report driver position |
| GET | `/api/location/nearby` | Query nearby drivers |
| GET | `/api/location/driver/{id}` | Get single driver position |

### Events Published
| Topic | When |
|-------|------|
| `driver.location.updated` | Every GPS update |

---

## 5. Matching Service

| Attribute       | Detail |
|-----------------|--------|
| **Module**      | `matching-service` |
| **Framework**   | Spring Boot |
| **Data Store**  | In-memory (stateless) |

### Responsibilities
- Consume `trip.requested` events
- Query Location Service for nearby **ONLINE** drivers
- Rank candidates by:  proximity → rating → acceptance rate
- Select best driver and publish `trip.matched` / `driver.requested`
- Handle timeout: if no driver accepts within 30 s, re-match or cancel

### Events Consumed
| Topic | Action |
|-------|--------|
| `trip.requested` | Start matching algorithm |
| `driver.status.changed` | Update available-driver pool |

### Events Published
| Topic | When |
|-------|------|
| `trip.matched` | Best driver selected |
| `driver.requested` | Push notification to driver |
| `trip.no_driver` | Timeout, no driver available |

---

## 6. WS-Driver Service

| Attribute       | Detail |
|-----------------|--------|
| **Module**      | `ws-driver-service` |
| **Framework**   | Spring Boot (WebFlux WebSocket) |

### Responsibilities
- Maintain persistent WebSocket connections with driver apps
- Receive driver GPS stream → forward to Location Service
- Push trip request notifications to drivers
- Push status-change events to drivers
- Connection lifecycle (heartbeat, reconnect)

### WebSocket Channels
| Path | Direction | Purpose |
|------|-----------|---------|
| `/ws/location` | Driver → Server | GPS stream |
| `/ws/driver/notifications` | Server → Driver | Trip offers, status updates |

---

## 7. WS-Rider Service

| Attribute       | Detail |
|-----------------|--------|
| **Module**      | `ws-rider-service` |
| **Framework**   | Spring Boot (WebFlux WebSocket) |

### Responsibilities
- Maintain persistent WebSocket connections with rider apps
- Push driver-assigned notifications
- Push real-time driver location (trip tracking)
- Push trip status changes (EN_ROUTE, ARRIVED, COMPLETED)
- Push payment/receipt notifications

### WebSocket Channels
| Path | Direction | Purpose |
|------|-----------|---------|
| `/ws/track/{tripId}` | Server → Rider | Live driver position |
| `/ws/rider/notifications` | Server → Rider | Status changes, payment |

---

## Cross-Cutting Concerns

| Concern | Implementation |
|---------|---------------|
| **Service Discovery** | Eureka (all services register) |
| **Config** | Spring Cloud Config or `application.yml` per service |
| **Auth** | JWT validated at Gateway, forwarded downstream |
| **Observability** | Spring Boot Actuator (`/actuator/health`, `/actuator/metrics`) |
| **Resilience** | Resilience4j circuit breakers at Gateway |
| **Rate Limiting** | Redis token-bucket at Gateway |
| **Containerisation** | Docker Compose (dev), Kubernetes (prod) |
| **Message Broker** | Apache Kafka – 3-broker cluster |
