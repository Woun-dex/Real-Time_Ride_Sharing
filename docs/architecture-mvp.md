# Architecture – MVP

> High-level architecture diagram for the Ride Sharing Platform MVP.
>
> **Note:** Since a binary `.png` can't be generated in-repo, this file contains
> a **Mermaid** diagram that renders in GitHub, GitLab, and VS Code (with the
> Mermaid extension). To export as PNG, paste the diagram into
> [mermaid.live](https://mermaid.live) or run `mmdc -i architecture-mvp.md -o architecture-mvp.png`.

---

## System Context

```mermaid
graph TB
    subgraph Clients
        RA[Rider App<br/>iOS / Android]
        DA[Driver App<br/>iOS / Android]
    end

    GW[Gateway Service<br/>:8080<br/>Spring Cloud Gateway]

    RA -- "REST / WS" --> GW
    DA -- "REST / WS" --> GW
```

---

## Container Diagram

```mermaid
graph LR
    subgraph Clients
        RA[Rider App]
        DA[Driver App]
    end

    subgraph API Layer
        GW[Gateway Service<br/>:8080]
    end

    subgraph Core Services
        US[User Service<br/>:8081]
        TS[Trip Service<br/>:8082]
        LS[Location Service<br/>:8083]
        MS[Matching Service<br/>:8084]
    end

    subgraph WebSocket Services
        WD[WS-Driver Service<br/>:8085]
        WR[WS-Rider Service<br/>:8086]
    end

    subgraph Infrastructure
        PG[(PostgreSQL)]
        RD[(Redis)]
        KF[/Apache Kafka<br/>3 Brokers\]
        EU[Eureka<br/>Discovery]
    end

    RA --> GW
    DA --> GW

    GW --> US
    GW --> TS
    GW --> LS
    GW --> WD
    GW --> WR

    US --> PG
    TS --> PG
    LS --> RD

    US --> KF
    TS --> KF
    LS --> KF
    MS --> KF
    WD --> KF
    WR --> KF

    GW -.-> EU
    US -.-> EU
    TS -.-> EU
    LS -.-> EU
    MS -.-> EU
    WD -.-> EU
    WR -.-> EU
```

---

## Trip Lifecycle – Sequence Diagram

```mermaid
sequenceDiagram
    participant R as Rider App
    participant GW as Gateway
    participant TS as Trip Service
    participant K as Kafka
    participant MS as Matching Service
    participant LS as Location Service
    participant NS as WS-Driver Service
    participant D as Driver App
    participant NR as WS-Rider Service

    R->>GW: POST /api/trips/request
    GW->>TS: forward
    TS->>TS: Create trip (REQUESTED)
    TS->>K: trip.requested

    K->>MS: consume trip.requested
    MS->>LS: GET /api/location/nearby?lat=..&lng=..&radius=5km
    LS-->>MS: [driver-001, driver-002]
    MS->>MS: Rank drivers (proximity, rating)
    MS->>K: driver.requested (driver-001)

    K->>NS: consume driver.requested
    NS->>D: WebSocket push (trip offer)

    D->>GW: POST /api/trips/{id}/accept
    GW->>TS: forward
    TS->>TS: Status → ACCEPTED
    TS->>K: trip.accepted

    K->>NR: consume trip.accepted
    NR->>R: WebSocket push (driver info + ETA)

    Note over D,R: Driver en route → Arrived → Trip starts

    D->>GW: PUT /api/trips/{id}/status (COMPLETED)
    GW->>TS: forward
    TS->>TS: Calculate fare
    TS->>K: trip.completed + payment.requested

    K->>NR: consume trip.completed
    NR->>R: WebSocket push (fare summary)
```

---

## Location Tracking – Sequence Diagram

```mermaid
sequenceDiagram
    participant D as Driver App
    participant GW as Gateway
    participant WD as WS-Driver Service
    participant LS as Location Service
    participant RD as Redis
    participant K as Kafka
    participant TS as Trip Service
    participant WR as WS-Rider Service
    participant R as Rider App

    D->>GW: WebSocket CONNECT /ws/location
    GW->>WD: upgrade
    loop Every 3 seconds
        D->>WD: {lat, lng, speed, heading}
        WD->>LS: forward location
        LS->>RD: GEOADD driver:locations ...
        LS->>K: driver.location.updated
        K->>TS: recalculate ETA
        K->>WR: push to rider channel
        WR->>R: {lat, lng, eta}
    end
```

---

## Infrastructure Topology (Docker Compose)

```mermaid
graph TB
    subgraph Docker Compose
        direction TB
        subgraph Kafka Cluster
            B1[Broker 1<br/>:9092]
            B2[Broker 2<br/>:9093]
            B3[Broker 3<br/>:9094]
        end
        PG[(PostgreSQL<br/>:5432)]
        RD[(Redis<br/>:6379)]
        EU[Eureka<br/>:8761]
        GW[Gateway<br/>:8080]
        US[User Svc<br/>:8081]
        TS[Trip Svc<br/>:8082]
        LS[Location Svc<br/>:8083]
        MS[Matching Svc<br/>:8084]
        WD[WS-Driver<br/>:8085]
        WR[WS-Rider<br/>:8086]
    end
```

---

## Technology Stack Summary

| Layer | Technology |
|-------|-----------|
| API Gateway | Spring Cloud Gateway (Netty) |
| Core Services | Spring Boot 3.2, Java 17 |
| Reactive WebSocket | Spring WebFlux |
| Service Discovery | Netflix Eureka |
| Message Broker | Apache Kafka (KRaft mode, 3 brokers) |
| Primary Database | PostgreSQL 16 |
| Cache / Geo-Index | Redis 7 |
| Resilience | Resilience4j (circuit breaker) |
| Rate Limiting | Spring Cloud Gateway + Redis |
| Containerisation | Docker Compose (dev), Kubernetes (prod) |
| Auth | JWT (issued by User Service) |
| Build | Maven |

---

## Port Assignments

| Service | Port |
|---------|------|
| Gateway | 8080 |
| User Service | 8081 |
| Trip Service | 8082 |
| Location Service | 8083 |
| Matching Service | 8084 |
| WS-Driver Service | 8085 |
| WS-Rider Service | 8086 |
| Eureka | 8761 |
| Kafka Broker 1 | 9092 |
| Kafka Broker 2 | 9093 |
| Kafka Broker 3 | 9094 |
| PostgreSQL | 5432 |
| Redis | 6379 |
