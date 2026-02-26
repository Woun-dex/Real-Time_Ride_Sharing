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

Notes:
- Each service is a Maven project located in its respective folder.
- Tests may be skipped during quick builds using the standard Maven flags.

Contributing
- Open a PR with a clear description of the change.
