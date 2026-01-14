package com.woundex.trip_service.domain.value_object;

import java.util.Objects;

public  final class Location {
        private final double lat;
        private final double lng;
        public Location(double lat, double lng) {
            if (lat < -90 || lat > 90) throw new IllegalArgumentException("latitude out of range");
            if (lng < -180 || lng > 180) throw new IllegalArgumentException("longitude out of range");
            this.lat = lat; this.lng = lng;
        }
        public double lat() { return lat; }
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
