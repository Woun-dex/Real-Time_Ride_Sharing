package com.woundex.location_service.domain.model;

public final class Position {
    private final double lat;
    private final double lon;

    public Position(double lat, double lon) {
        if (lat < -90.0 || lat > 90.0) throw new IllegalArgumentException("lat out of range");
        if (lon < -180.0 || lon > 180.0) throw new IllegalArgumentException("lon out of range");
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() { return lat; }
    public double getLon() { return lon; }
}