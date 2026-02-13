# Gateway Service

API Gateway for the Ride Sharing application — single entry point for **Rider** and **Driver** apps.

## Features

- **HTTP Routing** — Routes REST traffic to downstream microservices via Eureka service discovery
- **WebSocket Routing** — Proxies WebSocket connections to `ws-rider-service` and `ws-driver-service`
- **Circuit Breaker** — Resilience4j circuit breakers with fallback responses per service
- **Rate Limiting** — IP-based rate limiting backed by Redis
- **CORS** — Global CORS configuration for mobile/web clients
- **Request Logging** — Logs every request with method, path, client IP, duration, and WebSocket detection
- **Health Checks** — Actuator endpoints exposed at `/actuator/health`

## Route Map

| Path              | Destination            | Type      |
|-------------------|------------------------|-----------|
| `/api/users/**`   | `user-service`         | HTTP      |
| `/api/trips/**`   | `trip-service`         | HTTP      |
| `/api/locations/**` | `location-service`   | HTTP      |
| `/api/matching/**`  | `matching-service`   | HTTP      |
| `/ws/rider/**`    | `ws-rider-service`     | WebSocket |
| `/ws/driver/**`   | `ws-driver-service`    | WebSocket |

## Configuration

| Property             | Default   | Description                 |
|----------------------|-----------|-----------------------------|
| `server.port`        | `8090`    | Gateway listen port         |
| Eureka               | `:8761`   | Service discovery           |
| Redis                | `:6379`   | Rate limiting backend       |

## Running

```bash
cd gateway-service
./mvnw spring-boot:run
```

The gateway will be available at `http://localhost:8090`.
