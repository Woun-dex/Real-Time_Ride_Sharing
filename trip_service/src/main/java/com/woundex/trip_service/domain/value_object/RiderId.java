package com.woundex.trip_service.domain.value_object;

import java.util.Objects;
import java.util.UUID;

public  final class RiderId {
        private final UUID value;
        public RiderId(UUID value) { this.value = Objects.requireNonNull(value); }
        public static RiderId of(UUID value) { return new RiderId(value); }
        @Override public String toString() { return value.toString(); }
        @Override public boolean equals(Object o) { return (o instanceof RiderId) && value.equals(((RiderId)o).value); }
        @Override public int hashCode() { return value.hashCode(); }
        public UUID value(){
            return value ;
        }
    }