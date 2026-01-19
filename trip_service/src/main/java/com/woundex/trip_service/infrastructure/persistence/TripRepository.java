package com.woundex.trip_service.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.woundex.trip_service.domain.entities.Trip_Entity;
import com.woundex.trip_service.domain.value_object.TripId;

public interface TripRepository extends JpaRepository<Trip_Entity, TripId> {

}
