package com.woundex.trip_service.domain.value_object;

import java.util.Objects;
import java.util.UUID;

public final class DriverId {
        private final UUID value;
        public DriverId(UUID value) { this.value = Objects.requireNonNull(value); }
        public static DriverId of(UUID value) { return new DriverId(value); }
        @Override public String toString() { return value.toString(); }
        @Override public boolean equals(Object o) { return (o instanceof DriverId) && value.equals(((DriverId)o).value); }
        @Override public int hashCode() { return value.hashCode(); }
        public UUID value() { return value; }
        
    }
