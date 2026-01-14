package com.woundex.user.entities;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "riders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiderEntity {

    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String passwordHash;
    

}
