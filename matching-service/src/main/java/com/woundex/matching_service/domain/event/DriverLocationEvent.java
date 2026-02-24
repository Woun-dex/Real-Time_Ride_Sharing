package com.woundex.matching_service.domain.event;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DriverLocationEvent {
    private String driverId;
    private double lat;
    private double lon;

    public DriverLocationEvent() {}

    public String getDriverId() { return driverId; }

    /**
     * ws-driver-service publishes driverId as nested {"value":"uuid"}.
     * Also supports flat string for direct callers.
     */
    @JsonSetter("driverId")
    public void setDriverId(Object raw) {
        if (raw instanceof Map) {
            Object val = ((Map<?, ?>) raw).get("value");
            this.driverId = val != null ? val.toString() : null;
        } else if (raw != null) {
            this.driverId = raw.toString();
        }
    }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }

    /**
     * ws-driver-service publishes location as nested {"lat":..., "lng":...}.
     * This setter unpacks it into flat lat/lon fields.
     */
    @JsonSetter("location")
    public void setLocation(Map<String, Object> location) {
        if (location != null) {
            Object latVal = location.get("lat");
            Object lngVal = location.get("lng");
            if (latVal instanceof Number) this.lat = ((Number) latVal).doubleValue();
            if (lngVal instanceof Number) this.lon = ((Number) lngVal).doubleValue();
        }
    }
}