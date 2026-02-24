package com.woundex.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String role;       // RIDER | DRIVER
    private String status;     // ONLINE | OFFLINE (drivers only)
    private String vehicleInfo;    // Drivers only
    private String licenseNumber;  // Drivers only
}
