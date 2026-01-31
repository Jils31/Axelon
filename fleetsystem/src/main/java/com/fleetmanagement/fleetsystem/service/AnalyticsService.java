package com.fleetmanagement.fleetsystem.service;

import com.fleetmanagement.fleetsystem.dto.*;
import com.fleetmanagement.fleetsystem.model.Driver;
import com.fleetmanagement.fleetsystem.model.Maintenance;
import com.fleetmanagement.fleetsystem.model.Trip;
import com.fleetmanagement.fleetsystem.model.Vehicle;
import com.fleetmanagement.fleetsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final TripRepository tripRepository;
    private final MaintenanceRepository maintenanceRepository;

    @Cacheable(value = "fleetUtilization")
    public FleetUtilizationResponse getFleetUtilization() {
        long total = vehicleRepository.count();
        long available = vehicleRepository.findByStatus(Vehicle.VehicleStatus.AVAILABLE, null).getTotalElements();
        long inUse = vehicleRepository.findByStatus(Vehicle.VehicleStatus.IN_USE, null).getTotalElements();
        long maintenance = vehicleRepository.findByStatus(Vehicle.VehicleStatus.MAINTENANCE, null).getTotalElements();
        long retired = vehicleRepository.findByStatus(Vehicle.VehicleStatus.RETIRED, null).getTotalElements();

        double utilizationRate = total > 0 ? (double) inUse / total * 100 : 0.0;

        return new FleetUtilizationResponse(total, available, inUse, maintenance, retired, utilizationRate);
    }

    @Cacheable(value = "driverPerformance")
    public List<DriverPerformanceResponse> getDriverPerformance() {
        List<Driver> drivers = driverRepository.findAll();
        List<DriverPerformanceResponse> performanceList = new ArrayList<>();

        for (Driver driver : drivers) {
            Double totalDistance = tripRepository.getTotalDistanceByVehicle(driver.getId());
            if (totalDistance == null) totalDistance = 0.0;

            Double totalFuel = tripRepository.getTotalFuelByVehicle(driver.getId());
            if (totalFuel == null) totalFuel = 0.0;

            Double avgFuelEfficiency = totalFuel > 0 ? totalDistance / totalFuel : 0.0;

            performanceList.add(new DriverPerformanceResponse(
                    driver.getId(),
                    driver.getUser().getName(),
                    driver.getTotalTrips(),
                    totalDistance,
                    totalFuel,
                    driver.getRating(),
                    avgFuelEfficiency
            ));
        }

        return performanceList;
    }

    @Cacheable(value = "vehicleStats")
    public List<VehicleStatsResponse> getVehicleStatistics() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<VehicleStatsResponse> statsList = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            Long tripCount = tripRepository.findByVehicleId(vehicle.getId(), null).getTotalElements();

            Double totalDistance = tripRepository.getTotalDistanceByVehicle(vehicle.getId());
            if (totalDistance == null) totalDistance = 0.0;

            Double totalFuel = tripRepository.getTotalFuelByVehicle(vehicle.getId());
            if (totalFuel == null) totalFuel = 0.0;

            Double maintenanceCost = maintenanceRepository.getTotalMaintenanceCostByVehicle(vehicle.getId());
            if (maintenanceCost == null) maintenanceCost = 0.0;

            Double fuelEfficiency = totalFuel > 0 ? totalDistance / totalFuel : 0.0;

            statsList.add(new VehicleStatsResponse(
                    vehicle.getId(),
                    vehicle.getRegistrationNumber(),
                    vehicle.getBrand(),
                    vehicle.getModel(),
                    tripCount,
                    totalDistance,
                    totalFuel,
                    maintenanceCost,
                    fuelEfficiency
            ));
        }

        return statsList;
    }

    // Monthly Trip Statistics
    public List<MonthlyTripStatsResponse> getMonthlyTripStats(int year) {
        List<Trip> allTrips = tripRepository.findAll();
        Map<String, List<Trip>> tripsByMonth = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (Trip trip : allTrips) {
            if (trip.getStartTime().getYear() == year) {
                String month = trip.getStartTime().format(formatter);
                tripsByMonth.computeIfAbsent(month, k -> new ArrayList<>()).add(trip);
            }
        }

        List<MonthlyTripStatsResponse> monthlyStats = new ArrayList<>();

        for (Map.Entry<String, List<Trip>> entry : tripsByMonth.entrySet()) {
            List<Trip> trips = entry.getValue();

            long totalTrips = trips.size();
            long completed = trips.stream().filter(t -> t.getStatus() == Trip.TripStatus.COMPLETED).count();
            long cancelled = trips.stream().filter(t -> t.getStatus() == Trip.TripStatus.CANCELLED).count();

            double totalDistance = trips.stream()
                    .filter(t -> t.getDistance() != null)
                    .mapToDouble(Trip::getDistance)
                    .sum();

            double totalFuel = trips.stream()
                    .filter(t -> t.getFuelUsed() != null)
                    .mapToDouble(Trip::getFuelUsed)
                    .sum();

            double avgDistance = completed > 0 ? totalDistance / completed : 0.0;

            monthlyStats.add(new MonthlyTripStatsResponse(
                    entry.getKey(),
                    totalTrips,
                    completed,
                    cancelled,
                    totalDistance,
                    totalFuel,
                    avgDistance
            ));
        }

        monthlyStats.sort(Comparator.comparing(MonthlyTripStatsResponse::getMonth));
        return monthlyStats;
    }

    // Maintenance Cost Analysis
    public MaintenanceCostAnalysisResponse getMaintenanceCostAnalysis() {
        List<Maintenance> allMaintenance = maintenanceRepository.findAll();

        double totalCost = allMaintenance.stream()
                .filter(m -> m.getStatus() == Maintenance.MaintenanceStatus.COMPLETED)
                .mapToDouble(Maintenance::getCost)
                .sum();

        long totalRecords = allMaintenance.stream()
                .filter(m -> m.getStatus() == Maintenance.MaintenanceStatus.COMPLETED)
                .count();

        long totalVehicles = vehicleRepository.count();
        double avgCostPerVehicle = totalVehicles > 0 ? totalCost / totalVehicles : 0.0;

        Map<String, Double> costByType = allMaintenance.stream()
                .filter(m -> m.getStatus() == Maintenance.MaintenanceStatus.COMPLETED)
                .collect(Collectors.groupingBy(
                        m -> m.getType().name(),
                        Collectors.summingDouble(Maintenance::getCost)
                ));

        Map<String, Long> countByType = allMaintenance.stream()
                .filter(m -> m.getStatus() == Maintenance.MaintenanceStatus.COMPLETED)
                .collect(Collectors.groupingBy(
                        m -> m.getType().name(),
                        Collectors.counting()
                ));

        return new MaintenanceCostAnalysisResponse(
                totalCost,
                avgCostPerVehicle,
                totalRecords,
                costByType,
                countByType
        );
    }

    // Top Performing Drivers
    public List<DriverPerformanceResponse> getTopDrivers(int limit) {
        List<DriverPerformanceResponse> performance = getDriverPerformance();
        return performance.stream()
                .sorted(Comparator.comparingDouble(DriverPerformanceResponse::getRating).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Vehicles needing maintenance
    public List<VehicleResponse> getVehiclesNeedingMaintenance() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<VehicleResponse> needMaintenance = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            if (vehicle.getNextMaintenanceDate() != null &&
                    vehicle.getNextMaintenanceDate().isBefore(java.time.LocalDate.now().plusDays(7))) {
                needMaintenance.add(mapVehicleToResponse(vehicle));
            }
        }

        return needMaintenance;
    }

    private VehicleResponse mapVehicleToResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getRegistrationNumber(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getType().name(),
                vehicle.getStatus().name(),
                vehicle.getPurchaseDate(),
                vehicle.getLastMaintenanceDate(),
                vehicle.getNextMaintenanceDate(),
                vehicle.getFuelCapacity(),
                vehicle.getSeatingCapacity(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt()
        );
    }
}