package com.woundex.location_service.application.interfaces;

import com.woundex.location_service.application.dto.DriverLocationDto;

public interface IngestDriverLocation {
    void ingest(DriverLocationDto dto);
}