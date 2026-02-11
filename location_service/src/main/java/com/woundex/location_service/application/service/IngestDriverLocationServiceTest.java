package main.java.com.woundex.location_service.application.service;

import com.woundex.location_service.application.dto.DriverLocationDto;
import com.woundex.location_service.domain.model.DriverLocation;
import com.woundex.location_service.domain.repository.DriverLocationRepository;
import com.woundex.location_service.domain.service.LocationDomainService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class IngestDriverLocationServiceTest {

    @Test
    void ingest_should_mapDtoToDomain_and_callRepository() {
        DriverLocationRepository repo = Mockito.mock(DriverLocationRepository.class);
        LocationDomainService domainService = new LocationDomainService(repo);
        IngestDriverLocationService svc = new IngestDriverLocationService(domainService);

        String id = UUID.randomUUID().toString();
        long ts = Instant.now().toEpochMilli();

        DriverLocationDto dto = new DriverLocationDto();
        dto.setDriverId(id);
        dto.setLat(12.345678);
        dto.setLon(98.765432);
        dto.setTimestamp(ts);

        svc.ingest(dto);

        ArgumentCaptor<DriverLocation> captor = ArgumentCaptor.forClass(DriverLocation.class);
        Mockito.verify(repo).upsert(captor.capture());

        DriverLocation captured = captor.getValue();
        assertEquals(UUID.fromString(id), captured.getDriverId());
        assertEquals(12.345678, captured.getPosition().getLat(), 1e-6);
        assertEquals(98.765432, captured.getPosition().getLon(), 1e-6);
        assertEquals(ts, captured.getTimestamp().toEpochMilli());
    }
}