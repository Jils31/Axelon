package com.fleetmanagement.fleetsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String licenseNumber;
    private LocalDate licenseExpiry;
    private String phoneNumber;
    private String address;
    private LocalDate dateOfBirth;
    private String status;
    private Double rating;
    private Integer totalTrips;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}