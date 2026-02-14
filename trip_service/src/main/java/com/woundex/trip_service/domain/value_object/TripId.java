package com.woundex.trip_service.domain.value_object;

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public final class TripId {
        private final UUID value;
        @JsonCreator
        public TripId(String value) { this.value = Objects.requireNonNull(UUID.fromString(value)); }
        public static TripId of(String value) { return new TripId(value); }
        public static TripId generate() { return new TripId(UUID.randomUUID().toString()); }
        @Override public String toString() { return value.toString(); }
        @Override public boolean equals(Object o) { return (o instanceof TripId) && value.equals(((TripId)o).value); }
        @Override public int hashCode() { return value.hashCode(); }
        @JsonValue
        public UUID value(){
            return value ;
        }
    }