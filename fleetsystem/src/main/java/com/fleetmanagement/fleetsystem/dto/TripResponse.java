package com.fleetmanagement.fleetsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripResponse {

    private Long id;
    private Long vehicleId;
    private String vehicleRegistration;
    private String vehicleBrand;
    private String vehicleModel;
    private Long driverId;
    private String driverName;
    private String driverPhone;
    private String startLocation;
    private String endLocation;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double distance;
    private Double fuelUsed;
    private String status;
    private String purpose;
    private String notes;
    private Long durationMinutes; // Calculated
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}