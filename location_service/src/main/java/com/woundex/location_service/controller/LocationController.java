package com.woundex.location_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woundex.location_service.domain.model.NearbyDriver;
import com.woundex.location_service.domain.model.Position;
import com.woundex.location_service.infra.redis.RedisGeoService;

@RestController
@RequestMapping("/api/drivers")
public class LocationController {

    private final RedisGeoService geo;

    public LocationController(RedisGeoService geo) { this.geo = geo; }

    @GetMapping("/nearby")
    public ResponseEntity<List<NearbyDriver>> nearby(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "1000") double radiusMeters,
            @RequestParam(defaultValue = "50") int limit) {

        List<NearbyDriver> drivers = geo.findNearby(new Position(lat, lon), radiusMeters, limit);
        return ResponseEntity.ok(drivers);
    }
}