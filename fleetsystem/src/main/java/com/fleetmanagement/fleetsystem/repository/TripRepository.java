package com.fleetmanagement.fleetsystem.repository;

import com.fleetmanagement.fleetsystem.model.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    // Find trips by vehicle
    Page<Trip> findByVehicleId(Long vehicleId, Pageable pageable);

    // Find trips by driver
    Page<Trip> findByDriverId(Long driverId, Pageable pageable);

    // Find trips by status
    Page<Trip> findByStatus(Trip.TripStatus status, Pageable pageable);

    // Check if vehicle is available (no active trips)
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN false ELSE true END FROM Trip t " +
            "WHERE t.vehicle.id = :vehicleId AND t.status IN ('SCHEDULED', 'IN_PROGRESS')")
    boolean isVehicleAvailable(@Param("vehicleId") Long vehicleId);

    // Check if driver is available (no active trips)
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN false ELSE true END FROM Trip t " +
            "WHERE t.driver.id = :driverId AND t.status IN ('SCHEDULED', 'IN_PROGRESS')")
    boolean isDriverAvailable(@Param("driverId") Long driverId);

    // Find trips by date range
    @Query("SELECT t FROM Trip t WHERE t.startTime BETWEEN :startDate AND :endDate")
    Page<Trip> findTripsByDateRange(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate,
                                    Pageable pageable);

    // Get total distance by vehicle
    @Query("SELECT COALESCE(SUM(t.distance), 0) FROM Trip t WHERE t.vehicle.id = :vehicleId AND t.status = 'COMPLETED'")
    Double getTotalDistanceByVehicle(@Param("vehicleId") Long vehicleId);

    // Get total fuel used by vehicle
    @Query("SELECT COALESCE(SUM(t.fuelUsed), 0) FROM Trip t WHERE t.vehicle.id = :vehicleId AND t.status = 'COMPLETED'")
    Double getTotalFuelByVehicle(@Param("vehicleId") Long vehicleId);

    // Get completed trips count by driver
    @Query("SELECT COUNT(t) FROM Trip t WHERE t.driver.id = :driverId AND t.status = 'COMPLETED'")
    Long getCompletedTripsCountByDriver(@Param("driverId") Long driverId);

    // Find active trips
    List<Trip> findByStatusIn(List<Trip.TripStatus> statuses);
}