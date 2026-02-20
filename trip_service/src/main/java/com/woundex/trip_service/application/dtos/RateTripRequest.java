package com.woundex.trip_service.application.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record RateTripRequest(
    @Min(1) @Max(5) int rating
) {}
