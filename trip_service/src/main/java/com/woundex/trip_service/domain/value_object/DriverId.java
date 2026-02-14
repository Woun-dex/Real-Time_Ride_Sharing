package com.woundex.trip_service.domain.value_object;

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public final class DriverId {
        private final UUID value;
        @JsonCreator
        public DriverId(UUID value) { this.value = Objects.requireNonNull(value); }
        public static DriverId of(UUID value) { return new DriverId(value); }
        @Override public String toString() { return value.toString(); }
        @Override public boolean equals(Object o) { return (o instanceof DriverId) && value.equals(((DriverId)o).value); }
        @Override public int hashCode() { return value.hashCode(); }
        @JsonValue
        public UUID value() { return value; }
        
    }
