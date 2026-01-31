package com.fleetmanagement.fleetsystem.controller;

import com.fleetmanagement.fleetsystem.dto.*;
import com.fleetmanagement.fleetsystem.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Analytics", description = "Fleet analytics and reporting APIs")
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Operation(summary = "Get fleet utilization", description = "Get current fleet utilization statistics")
    @GetMapping("/fleet-utilization")
    public ResponseEntity<FleetUtilizationResponse> getFleetUtilization() {
        FleetUtilizationResponse response = analyticsService.getFleetUtilization();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get driver performance", description = "Get performance metrics for all drivers")
    @GetMapping("/driver-performance")
    public ResponseEntity<List<DriverPerformanceResponse>> getDriverPerformance() {
        List<DriverPerformanceResponse> response = analyticsService.getDriverPerformance();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get vehicle statistics",
            description = "Retrieve statistics for all vehicles including usage, mileage, and status"
    )
    @GetMapping("/vehicle-statistics")
    public ResponseEntity<List<VehicleStatsResponse>> getVehicleStatistics() {
        List<VehicleStatsResponse> response = analyticsService.getVehicleStatistics();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get monthly trip statistics",
            description = "Get month-wise trip statistics for a given year"
    )
    @GetMapping("/monthly-trip-stats")
    public ResponseEntity<List<MonthlyTripStatsResponse>> getMonthlyTripStats(
            @RequestParam int year) {
        List<MonthlyTripStatsResponse> response = analyticsService.getMonthlyTripStats(year);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get maintenance cost analysis",
            description = "Analyze maintenance costs across the fleet and identify cost-heavy vehicles"
    )
    @GetMapping("/maintenance-cost-analysis")
    public ResponseEntity<MaintenanceCostAnalysisResponse> getMaintenanceCostAnalysis() {
        MaintenanceCostAnalysisResponse response = analyticsService.getMaintenanceCostAnalysis();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get top performing drivers",
            description = "Retrieve top drivers based on performance metrics with a configurable limit"
    )
    @GetMapping("/top-drivers")
    public ResponseEntity<List<DriverPerformanceResponse>> getTopDrivers(
            @RequestParam(defaultValue = "5") int limit) {
        List<DriverPerformanceResponse> response = analyticsService.getTopDrivers(limit);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get vehicles needing maintenance",
            description = "List vehicles that are due or overdue for maintenance based on system rules"
    )
    @GetMapping("/vehicles-needing-maintenance")
    public ResponseEntity<List<VehicleResponse>> getVehiclesNeedingMaintenance() {
        List<VehicleResponse> response = analyticsService.getVehiclesNeedingMaintenance();
        return ResponseEntity.ok(response);
    }
}