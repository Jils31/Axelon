package com.fleetmanagement.fleetsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyTripStatsResponse {
    private String month;
    private Long totalTrips;
    private Long completedTrips;
    private Long cancelledTrips;
    private Double totalDistance;
    private Double totalFuelUsed;
    private Double averageTripDistance;
}