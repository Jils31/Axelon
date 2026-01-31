package com.fleetmanagement.fleetsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverPerformanceResponse {
    private Long driverId;
    private String driverName;
    private Integer totalTrips;
    private Double totalDistance;
    private Double totalFuelUsed;
    private Double rating;
    private Double averageFuelEfficiency;
}