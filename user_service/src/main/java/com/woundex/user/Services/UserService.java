package com.woundex.user.Services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.woundex.user.Repositories.DriverRepository;
import com.woundex.user.Repositories.RiderRepository;
import com.woundex.user.dto.DriverStatusRequest;
import com.woundex.user.dto.LoginRequest;
import com.woundex.user.dto.LoginResponse;
import com.woundex.user.dto.SignUpRequest;
import com.woundex.user.dto.UpdateProfileRequest;
import com.woundex.user.dto.UserProfileResponse;
import com.woundex.user.entities.DriverEntity;
import com.woundex.user.entities.RiderEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

    private final DriverRepository driverRepository;
    private final RiderRepository riderRepository;
    private final PasswordEncoder passwordEncoder;

    // POST /api/users/register
    public void registerUser(SignUpRequest request) {
        String role = request.getRole();
        String passwordHash = passwordEncoder.encode(request.getPassword());

        if ("DRIVER".equalsIgnoreCase(role)) {
            DriverEntity driver = DriverEntity.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .passwordHash(passwordHash)
                    .licenseNumber(request.getLicence_number())
                    .vehicleInfo(request.getVehicle_info())
                    .build();
            driverRepository.save(driver);
        } else if ("RIDER".equalsIgnoreCase(role)) {
            RiderEntity rider = RiderEntity.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .passwordHash(passwordHash)
                    .build();
            riderRepository.save(rider);
        } else {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    // POST /api/auth/login
    public LoginResponse loginUser(LoginRequest request) {
        String role = request.getRole();

        if ("DRIVER".equalsIgnoreCase(role)) {
            return loginAsDriver(request.getEmail(), request.getPassword());
        } else if ("RIDER".equalsIgnoreCase(role)) {
            return loginAsRider(request.getEmail(), request.getPassword());
        }

        // No role supplied — auto-detect
        if (driverRepository.findByEmailAndIsDeletedFalse(request.getEmail()).isPresent()) {
            return loginAsDriver(request.getEmail(), request.getPassword());
        }
        if (riderRepository.findByEmailAndIsDeletedFalse(request.getEmail()).isPresent()) {
            return loginAsRider(request.getEmail(), request.getPassword());
        }
        throw new IllegalArgumentException("No account found for this email");
    }

    private LoginResponse loginAsDriver(String email, String password) {
        DriverEntity driver = driverRepository
                .findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!passwordEncoder.matches(password, driver.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return new LoginResponse(driver.getId().toString(), driver.getName(), driver.getEmail(), "DRIVER");
    }

    private LoginResponse loginAsRider(String email, String password) {
        RiderEntity rider = riderRepository
                .findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!passwordEncoder.matches(password, rider.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return new LoginResponse(rider.getId().toString(), rider.getName(), rider.getEmail(), "RIDER");
    }

    // GET /api/users/{id}
    public UserProfileResponse getProfile(UUID id) {
        var driverOpt = driverRepository.findById(id);
        if (driverOpt.isPresent() && !driverOpt.get().getIsDeleted()) {
            DriverEntity d = driverOpt.get();
            return UserProfileResponse.builder()
                    .userId(d.getId().toString())
                    .name(d.getName())
                    .email(d.getEmail())
                    .phone(d.getPhone())
                    .role("DRIVER")
                    .status(d.getStatus())
                    .vehicleInfo(d.getVehicleInfo())
                    .licenseNumber(d.getLicenseNumber())
                    .build();
        }
        RiderEntity r = riderRepository.findById(id)
                .filter(rider -> !rider.getIsDeleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return UserProfileResponse.builder()
                .userId(r.getId().toString())
                .name(r.getName())
                .email(r.getEmail())
                .phone(r.getPhone())
                .role("RIDER")
                .build();
    }

    // PUT /api/users/{id}
    public UserProfileResponse updateProfile(UUID id, UpdateProfileRequest req) {
        var driverOpt = driverRepository.findById(id);
        if (driverOpt.isPresent() && !driverOpt.get().getIsDeleted()) {
            DriverEntity d = driverOpt.get();
            if (req.getName()  != null) d.setName(req.getName());
            if (req.getPhone() != null) d.setPhone(req.getPhone());
            driverRepository.save(d);
            return getProfile(id);
        }
        RiderEntity r = riderRepository.findById(id)
                .filter(rider -> !rider.getIsDeleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (req.getName()  != null) r.setName(req.getName());
        if (req.getPhone() != null) r.setPhone(req.getPhone());
        riderRepository.save(r);
        return getProfile(id);
    }

    // DELETE /api/users/{id}
    public void softDeleteAccount(UUID id) {
        var driverOpt = driverRepository.findById(id);
        if (driverOpt.isPresent() && !driverOpt.get().getIsDeleted()) {
            DriverEntity d = driverOpt.get();
            d.setIsDeleted(true);
            d.setDeletedAt(LocalDateTime.now());
            driverRepository.save(d);
            return;
        }
        RiderEntity r = riderRepository.findById(id)
                .filter(rider -> !rider.getIsDeleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        r.setIsDeleted(true);
        r.setDeletedAt(LocalDateTime.now());
        riderRepository.save(r);
    }

    /** Look up a driver's UUID by email. */
    public UUID findDriverIdByEmail(String email) {
        return driverRepository.findByEmailAndIsDeletedFalse(email)
                .map(DriverEntity::getId)
                .orElseThrow(() -> new IllegalArgumentException("No active driver found for email: " + email));
    }

    // PUT /api/drivers/status
    public UserProfileResponse toggleDriverStatus(UUID id, DriverStatusRequest req) {
        DriverEntity d = driverRepository.findById(id)
                .filter(driver -> !driver.getIsDeleted())
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));
        d.setStatus(req.getStatus().toUpperCase());
        driverRepository.save(d);
        return getProfile(id);
    }
}
