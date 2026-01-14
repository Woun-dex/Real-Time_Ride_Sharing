package com.woundex.trip_service.domain.value_object;

import java.util.Objects;

public  final class RiderId {
        private final String value;
        public RiderId(String value) { this.value = Objects.requireNonNull(value); }
        public static RiderId of(String value) { return new RiderId(value); }
        @Override public String toString() { return value; }
        @Override public boolean equals(Object o) { return (o instanceof RiderId) && value.equals(((RiderId)o).value); }
        @Override public int hashCode() { return value.hashCode(); }
    }