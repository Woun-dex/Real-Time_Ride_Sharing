package com.woundex.user.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthPrincipal {
    private String userId;
    private String email;
    private String role;
}
