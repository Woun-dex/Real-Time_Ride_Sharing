package com.woundex.trip_service.application.dtos;

import java.util.UUID;

import com.woundex.trip_service.domain.value_object.DriverId;

import jakarta.validation.constraints.NotNull;

public record AssignDriverRequest(
    UUID driverId
) {
}
