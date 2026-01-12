package com.woundex.user.entities;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverEntity {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String passwordHash;
    private String licenceNumber;
    private String vehicleInfo;
}
