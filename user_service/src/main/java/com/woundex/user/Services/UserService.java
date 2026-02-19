package com.woundex.user.Services;

import java.time.LocalDateTime;

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
import com.woundex.user.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

    private final DriverRepository driverRepository;
    private final RiderRepository riderRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ------------------------------------------------------------------ //
    // POST /api/users/register
    // ------------------------------------------------------------------ //
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

    // ------------------------------------------------------------------ //
    // POST /api/auth/login
    // ------------------------------------------------------------------ //
    public LoginResponse loginUser(LoginRequest request) {
        String role = request.getRole();

        if ("DRIVER".equalsIgnoreCase(role)) {
            DriverEntity driver = driverRepository
                    .findByEmailAndIsDeletedFalse(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Driver not found"));

            if (!passwordEncoder.matches(request.getPassword(), driver.getPasswordHash())) {
                throw new IllegalArgumentException("Invalid password");
            }
            String token = jwtUtil.generateToken(
                    driver.getId().toString(), driver.getEmail(), "DRIVER");
            return new LoginResponse(
                    driver.getId().toString(), driver.getName(), driver.getEmail(),
                    "DRIVER", token, jwtUtil.getExpirationSeconds());

        } else if ("RIDER".equalsIgnoreCase(role)) {
            RiderEntity rider = riderRepository
                    .findByEmailAndIsDeletedFalse(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Rider not found"));

            if (!passwordEncoder.matches(request.getPassword(), rider.getPasswordHash())) {
                throw new IllegalArgumentException("Invalid password");
            }
            String token = jwtUtil.generateToken(
                    rider.getId().toString(), rider.getEmail(), "RIDER");
            return new LoginResponse(
                    rider.getId().toString(), rider.getName(), rider.getEmail(),
                    "RIDER", token, jwtUtil.getExpirationSeconds());

        } else {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    // ------------------------------------------------------------------ //
    // GET /api/users/me
    // ------------------------------------------------------------------ //
    public UserProfileResponse getProfile(String email, String role) {
        if ("DRIVER".equalsIgnoreCase(role)) {
            DriverEntity d = driverRepository
                    .findByEmailAndIsDeletedFalse(email)
                    .orElseThrow(() -> new IllegalArgumentException("Driver not found"));
            return UserProfileResponse.builder()
                    .userId(d.getId().toString())
                    .name(d.getName())
                    .email(d.getEmail())
                    .phone(d.getPhone())
                    .role("DRIVER")
                    .status(d.getStatus())
                    .build();
        } else {
            RiderEntity r = riderRepository
                    .findByEmailAndIsDeletedFalse(email)
                    .orElseThrow(() -> new IllegalArgumentException("Rider not found"));
            return UserProfileResponse.builder()
                    .userId(r.getId().toString())
                    .name(r.getName())
                    .email(r.getEmail())
                    .phone(r.getPhone())
                    .role("RIDER")
                    .build();
        }
    }

    // ------------------------------------------------------------------ //
    // PUT /api/users/me
    // ------------------------------------------------------------------ //
    public UserProfileResponse updateProfile(String email, String role, UpdateProfileRequest req) {
        if ("DRIVER".equalsIgnoreCase(role)) {
            DriverEntity d = driverRepository
                    .findByEmailAndIsDeletedFalse(email)
                    .orElseThrow(() -> new IllegalArgumentException("Driver not found"));
            if (req.getName()  != null) d.setName(req.getName());
            if (req.getPhone() != null) d.setPhone(req.getPhone());
            driverRepository.save(d);
        } else {
            RiderEntity r = riderRepository
                    .findByEmailAndIsDeletedFalse(email)
                    .orElseThrow(() -> new IllegalArgumentException("Rider not found"));
            if (req.getName()  != null) r.setName(req.getName());
            if (req.getPhone() != null) r.setPhone(req.getPhone());
            riderRepository.save(r);
        }
        return getProfile(email, role);
    }

    // ------------------------------------------------------------------ //
    // DELETE /api/users/me  (soft delete)
    // ------------------------------------------------------------------ //
    public void softDeleteAccount(String email, String role) {
        if ("DRIVER".equalsIgnoreCase(role)) {
            DriverEntity d = driverRepository
                    .findByEmailAndIsDeletedFalse(email)
                    .orElseThrow(() -> new IllegalArgumentException("Driver not found"));
            d.setIsDeleted(true);
            d.setDeletedAt(LocalDateTime.now());
            driverRepository.save(d);
        } else {
            RiderEntity r = riderRepository
                    .findByEmailAndIsDeletedFalse(email)
                    .orElseThrow(() -> new IllegalArgumentException("Rider not found"));
            r.setIsDeleted(true);
            r.setDeletedAt(LocalDateTime.now());
            riderRepository.save(r);
        }
    }

    // ------------------------------------------------------------------ //
    // PUT /api/drivers/status
    // ------------------------------------------------------------------ //
    public UserProfileResponse toggleDriverStatus(String email, DriverStatusRequest req) {
        DriverEntity d = driverRepository
                .findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));
        d.setStatus(req.getStatus().toUpperCase());
        driverRepository.save(d);
        return getProfile(email, "DRIVER");
    }
}
