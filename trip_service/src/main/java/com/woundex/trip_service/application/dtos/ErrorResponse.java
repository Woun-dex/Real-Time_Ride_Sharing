package com.woundex.trip_service.application.dtos;

import java.time.Instant;

public record ErrorResponse(
    String message,
    String error,
    int status,
    Instant timestamp
) {}


