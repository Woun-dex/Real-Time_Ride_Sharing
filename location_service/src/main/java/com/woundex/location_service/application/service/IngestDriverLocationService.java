package main.java.com.woundex.location_service.application.service;

import com.woundex.location_service.application.dto.DriverLocationDto;
import com.woundex.location_service.application.port.IngestDriverLocation;
import com.woundex.location_service.domain.model.Position;
import com.woundex.location_service.domain.service.LocationDomainService;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class IngestDriverLocationService implements IngestDriverLocation {

    private final LocationDomainService domainService;

    public IngestDriverLocationService(LocationDomainService domainService) {
        this.domainService = domainService;
    }

    @Override
    public void ingest(DriverLocationDto dto) {
        if (dto == null || dto.getDriverId() == null) return;
        UUID driverId;
        try {
            driverId = UUID.fromString(dto.getDriverId());
        } catch (IllegalArgumentException e) {
            return;
        }
        Position pos = new Position(dto.getLat(), dto.getLon());
        Instant ts = dto.getTimestamp() > 0 ? Instant.ofEpochMilli(dto.getTimestamp()) : Instant.now();
        domainService.ingest(driverId, pos, ts);
    }
}