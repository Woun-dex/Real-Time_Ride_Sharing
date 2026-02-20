package com.woundex.trip_service.application.dtos;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record AcceptTripRequest(
    @NotNull UUID driverId
) {}
