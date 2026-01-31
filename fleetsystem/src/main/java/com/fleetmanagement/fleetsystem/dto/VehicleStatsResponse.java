package com.fleetmanagement.fleetsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleStatsResponse {
    private Long vehicleId;
    private String registrationNumber;
    private String brand;
    private String model;
    private Long totalTrips;
    private Double totalDistance;
    private Double totalFuelUsed;
    private Double totalMaintenanceCost;
    private Double fuelEfficiency;
}