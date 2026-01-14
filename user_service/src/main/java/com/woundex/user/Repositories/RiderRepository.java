package com.woundex.user.Repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.woundex.user.entities.RiderEntity;

public interface RiderRepository extends JpaRepository<RiderEntity, UUID> {

    Optional<RiderEntity> findByEmail(String email);

    
}
