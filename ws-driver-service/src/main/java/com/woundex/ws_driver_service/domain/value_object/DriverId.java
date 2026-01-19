package com.woundex.ws_driver_service.domain.value_object;

import java.util.Objects;
import java.util.UUID;

public final class DriverId {
    private final UUID value;

    public DriverId(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    public static DriverId of(String value) {
        return new DriverId(UUID.fromString(value));
    }

    public static DriverId of(UUID value) {
        return new DriverId(value);
    }

    public static DriverId generate() {
        return new DriverId(UUID.randomUUID());
    }

    public UUID value() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof DriverId) && value.equals(((DriverId) o).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
