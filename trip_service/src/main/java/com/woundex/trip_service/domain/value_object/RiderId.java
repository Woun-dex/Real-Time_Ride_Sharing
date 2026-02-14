package com.woundex.trip_service.domain.value_object;

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public  final class RiderId {
        private final UUID value;
        @JsonCreator
        public RiderId(UUID value) { this.value = Objects.requireNonNull(value); }
        public static RiderId of(UUID value) { return new RiderId(value); }
        @Override public String toString() { return value.toString(); }
        @Override public boolean equals(Object o) { return (o instanceof RiderId) && value.equals(((RiderId)o).value); }
        @Override public int hashCode() { return value.hashCode(); }
        @JsonValue
        public UUID value(){
            return value ;
        }
    }