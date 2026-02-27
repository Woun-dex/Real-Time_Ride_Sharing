# Ride Sharing App

A simple microservices example for a ride-sharing platform.

Services included:
- gateway-service
- location_service
- matching-service
- trip_service
- user_service
- ws-driver-service
- ws-rider-service

Requirements:
- JDK 17 or later
- Maven (or use the provided `mvnw` / `mvnw.cmd` wrappers)
- Docker & Docker Compose (for running the full system)

Quick start (Docker Compose):
```bash
docker-compose up --build
```

Build a single service (Windows example):
```powershell
cd location_service
.\mvnw.cmd package
```


