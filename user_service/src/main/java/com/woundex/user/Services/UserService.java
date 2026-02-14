package com.woundex.user.Services;


import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.woundex.user.Repositories.DriverRepository;
import com.woundex.user.Repositories.RiderRepository;
import com.woundex.user.dto.LoginRequest;
import com.woundex.user.dto.LoginResponse;
import com.woundex.user.dto.SignUpRequest;
import com.woundex.user.entities.DriverEntity;
import com.woundex.user.entities.RiderEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
    
    private final DriverRepository driverRepository;
    private final RiderRepository riderRepository;

    public void registerDriver(SignUpRequest request) {


        String role = request.getRole();
        String password = request.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String passwordHash = encoder.encode(password);


        if (role.equals("DRIVER")) {
            // Save driver details to the database
            DriverEntity driver =  DriverEntity.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordHash)
                .licenseNumber(request.getLicence_number())
                .vehicleInfo(request.getVehicle_info())
                .build();
            // You would typically map SignUpRequest to DriverEntity here

            driverRepository.save(driver);
            // and then save it using driverRepository.save(driverEntity);
        } else if (role.equals("RIDER")) {
            // Save rider details to the database
            RiderEntity rider = RiderEntity.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordHash)
                .build();
            // You would typically map SignUpRequest to RiderEntity here
            riderRepository.save(rider);
            // and then save it using riderRepository.save(riderEntity);
        } else {
            throw new IllegalArgumentException("Invalid role: " + role);
        }

    }

    public LoginResponse loginUser(LoginRequest request) {
        String role = request.getRole();

        if (role.equals("DRIVER")) {
            // Authenticate driver
            DriverEntity driver = driverRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("Driver not found"));
            if ( driver != null) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                if (encoder.matches(request.getPassword(), driver.getPasswordHash())) {
                    return new LoginResponse(driver.getId().toString(), driver.getName(), driver.getEmail(), "DRIVER" , null, null);
                } else {
                    throw new IllegalArgumentException("Invalid password");
                }
            }
            // Fetch driver by email
            // Compare password hashes
            // Return appropriate LoginResponse
        } else if (role.equals("RIDER")) {
            // Authenticate rider
            RiderEntity rider = riderRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("Rider not found"));
            if ( rider != null) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                if (encoder.matches(request.getPassword(), rider.getPasswordHash())) {
                    return new LoginResponse(rider.getId().toString(), rider.getName(), rider.getEmail(), "RIDER" , null, null);
                } else {
                    throw new IllegalArgumentException("Invalid password");
                }
            }
            // Fetch rider by email
            // Compare password hashes
            // Return appropriate LoginResponse
        } else {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
        return null;
    }
    
}
