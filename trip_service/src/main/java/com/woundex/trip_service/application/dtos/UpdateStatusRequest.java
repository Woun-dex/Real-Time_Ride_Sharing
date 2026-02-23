package com.woundex.trip_service.application.dtos;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
    @NotNull String status,
    UUID driverId
) {}
