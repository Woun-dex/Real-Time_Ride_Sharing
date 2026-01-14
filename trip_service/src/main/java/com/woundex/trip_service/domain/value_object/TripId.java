package com.woundex.trip_service.domain.value_object;

import java.util.Objects;
import java.util.UUID;

public final class TripId {
        private final String value;
        private TripId(String value) { this.value = Objects.requireNonNull(value); }
        public static TripId of(String value) { return new TripId(value); }
        public static TripId generate() { return new TripId(UUID.randomUUID().toString()); }
        @Override public String toString() { return value; }
        @Override public boolean equals(Object o) { return (o instanceof TripId) && value.equals(((TripId)o).value); }
        @Override public int hashCode() { return value.hashCode(); }
    }