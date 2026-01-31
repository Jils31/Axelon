package com.fleetmanagement.fleetsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceCostAnalysisResponse {
    private Double totalMaintenanceCost;
    private Double averageCostPerVehicle;
    private Long totalMaintenanceRecords;
    private Map<String, Double> costByType;
    private Map<String, Long> countByType;
}