package com.woundex.trip_service.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.woundex.trip_service.domain.entities.Trip_Entity;

public interface TripRepository extends JpaRepository<Trip_Entity, UUID> {

}
