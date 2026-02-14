package com.woundex.matching_service.domain.event;


public final class Topics {
    private Topics() {}

    public static final String TRIP_REQUESTED     = "trip.requested";
    public static final String DRIVER_LOCATION     = "driver-locations";
    public static final String DRIVER_OFFER        = "driver.offer";
    public static final String DRIVER_ACCEPTED     = "driver.accepted";
    public static final String DRIVER_ASSIGNED     = "driver.assigned";
    public static final String DRIVER_OFFER_EXPIRED = "driver.offer.expired";
    public static final String NO_DRIVER_AVAILABLE  = "no.driver.available";
}