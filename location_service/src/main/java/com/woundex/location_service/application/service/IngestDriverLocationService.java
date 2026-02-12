package com.woundex.location_service.application.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.woundex.location_service.application.dto.DriverLocationDto;
import com.woundex.location_service.application.interfaces.IngestDriverLocation;
import com.woundex.location_service.domain.model.Position;
import com.woundex.location_service.domain.service.LocationDomainService;

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