# Matching Service DDD

## Overview
This project implements a matching service for a ride-sharing application using Domain-Driven Design (DDD) principles. The service matches riders with available drivers based on proximity and availability, utilizing Redis for managing driver status.

## Project Structure
```
matching-service-ddd
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── woundex
│   │   │           └── matching_service
│   │   │               ├── MatchingServiceApplication.java
│   │   │               ├── application
│   │   │               │   ├── port
│   │   │               │   │   ├── in
│   │   │               │   │   │   └── RequestTripUseCase.java
│   │   │               │   │   └── out
│   │   │               │   │       ├── DriverRepositoryPort.java
│   │   │               │   │       └── DriverStatusStorePort.java
│   │   │               │   ├── service
│   │   │               │   │   └── MatchingApplicationService.java
│   │   │               │   └── dto
│   │   │               │       ├── TripRequestDTO.java
│   │   │               │       └── DriverAssignmentDTO.java
│   │   │               ├── domain
│   │   │               │   ├── model
│   │   │               │   │   ├── TripRequest.java
│   │   │               │   │   ├── Driver.java
│   │   │               │   │   ├── Location.java
│   │   │               │   │   └── DriverStatus.java
│   │   │               │   ├── repository
│   │   │               │   │   └── DriverStatusRepository.java
│   │   │               │   └── service
│   │   │               │       ├── Matcher.java
│   │   │               │       └── ProximityMatcher.java
│   │   │               ├── infrastructure
│   │   │               │   ├── config
│   │   │               │   │   └── RedisConfig.java
│   │   │               │   ├── redis
│   │   │               │   │   ├── RedisDriverStatusRepository.java
│   │   │               │   │   └── model
│   │   │               │   │       └── RedisDriverStatus.java
│   │   │               │   └── messaging
│   │   │               │       └── DriverAssignedPublisher.java
│   │   │               └── adapters
│   │   │                   ├── inbound
│   │   │                   │   └── rest
│   │   │                   │       └── TripController.java
│   │   │                   └── outbound
│   │   │                       └── location
│   │   │                           └── DriverLocationClient.java
│   │   └── resources
│   │       └── application.yml
│   └── test
│       └── java
│           └── com
│               └── woundex
│                   └── matching_service
│                       └── MatchingServiceApplicationTests.java
├── pom.xml
└── README.md
```

## Setup Instructions
1. **Clone the repository**:
   ```
   git clone <repository-url>
   cd matching-service-ddd
   ```

2. **Build the project**:
   ```
   mvn clean install
   ```

3. **Run the application**:
   ```
   mvn spring-boot:run
   ```

4. **Configuration**:
   Update the `src/main/resources/application.yml` file with your Redis connection details.

## Usage
- **Trip Requests**: Send a POST request to the `/trips` endpoint with the trip details to request a ride.
- **Driver Assignments**: The service will automatically assign an available driver based on proximity and availability.

## Contributing
Contributions are welcome! Please submit a pull request or open an issue for any enhancements or bug fixes.

## License
This project is licensed under the MIT License.