package com.fleetmanagement.fleetsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FleetUtilizationResponse {
    private Long totalVehicles;
    private Long availableVehicles;
    private Long inUseVehicles;
    private Long maintenanceVehicles;
    private Long retiredVehicles;
    private Double utilizationRate;
}