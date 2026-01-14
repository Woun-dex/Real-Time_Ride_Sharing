package com.woundex.trip_service.domain.value_object;

import java.util.Objects;

public final class DriverId {
        private final String value;
        public DriverId(String value) { this.value = Objects.requireNonNull(value); }
        public static DriverId of(String value) { return new DriverId(value); }
        @Override public String toString() { return value; }
        @Override public boolean equals(Object o) { return (o instanceof DriverId) && value.equals(((DriverId)o).value); }
        @Override public int hashCode() { return value.hashCode(); }
    }
