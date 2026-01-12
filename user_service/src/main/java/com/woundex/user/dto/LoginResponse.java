package com.woundex.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

        private String userId;
        private String name;
        private String email;
        private String role; // RIDER / DRIVER
        private String accessToken; // JWT
        private Long expiresIn;     // in seconds

}
