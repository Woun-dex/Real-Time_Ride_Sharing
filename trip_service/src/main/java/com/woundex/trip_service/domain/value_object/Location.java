package com.woundex.trip_service.domain.value_object;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public  final class Location {
        private final double lat;
        private final double lng;
        @JsonCreator
        public Location(@JsonProperty("lat") double lat, @JsonProperty("lng") double lng) {
            if (lat < -90 || lat > 90) throw new IllegalArgumentException("latitude out of range");
            if (lng < -180 || lng > 180) throw new IllegalArgumentException("longitude out of range");
            this.lat = lat; this.lng = lng;
        }
        @JsonProperty("lat")
        public double lat() { return lat; }
        @JsonProperty("lng")
        public double lng() { return lng; }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Location)) return false;
            Location other = (Location) o;
            return Double.compare(lat, other.lat) == 0 && Double.compare(lng, other.lng) == 0;
        }
        @Override public int hashCode() { return Objects.hash(lat, lng); }
        @Override public String toString() { return "(" + lat + "," + lng + ")"; }
    }
