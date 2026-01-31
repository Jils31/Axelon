package com.fleetmanagement.fleetsystem.service;

import com.fleetmanagement.fleetsystem.dto.CompleteTripRequest;
import com.fleetmanagement.fleetsystem.dto.TripRequest;
import com.fleetmanagement.fleetsystem.dto.TripResponse;
import com.fleetmanagement.fleetsystem.model.Driver;
import com.fleetmanagement.fleetsystem.model.Trip;
import com.fleetmanagement.fleetsystem.model.Vehicle;
import com.fleetmanagement.fleetsystem.repository.DriverRepository;
import com.fleetmanagement.fleetsystem.repository.TripRepository;
import com.fleetmanagement.fleetsystem.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    // Create trip
    @Transactional
    public TripResponse createTrip(TripRequest request) {
        // Validate vehicle exists
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + request.getVehicleId()));

        // Validate driver exists
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + request.getDriverId()));

        // Check vehicle availability
        if (!tripRepository.isVehicleAvailable(request.getVehicleId())) {
            throw new RuntimeException("Vehicle is not available for the requested time");
        }

        // Check driver availability
        if (!tripRepository.isDriverAvailable(request.getDriverId())) {
            throw new RuntimeException("Driver is not available for the requested time");
        }

        // Check vehicle status
        if (vehicle.getStatus() != Vehicle.VehicleStatus.AVAILABLE) {
            throw new RuntimeException("Vehicle status must be AVAILABLE");
        }

        // Check driver status
        if (driver.getStatus() != Driver.DriverStatus.AVAILABLE) {
            throw new RuntimeException("Driver status must be AVAILABLE");
        }

        Trip trip = mapToEntity(request, vehicle, driver);
        Trip savedTrip = tripRepository.save(trip);

        // Update vehicle and driver status if trip is starting now or in progress
        if (request.getStatus().equals("IN_PROGRESS")) {
            vehicle.setStatus(Vehicle.VehicleStatus.IN_USE);
            driver.setStatus(Driver.DriverStatus.ON_TRIP);
            vehicleRepository.save(vehicle);
            driverRepository.save(driver);
        }

        return mapToResponse(savedTrip);
    }

    // Get trip by ID
    public TripResponse getTripById(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));

        return mapToResponse(trip);
    }

    // Get all trips with pagination
    public Page<TripResponse> getAllTrips(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<Trip> trips = tripRepository.findAll(pageable);

        return trips.map(this::mapToResponse);
    }

    // Start trip (change status to IN_PROGRESS)
    @Transactional
    public TripResponse startTrip(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));

        if (trip.getStatus() != Trip.TripStatus.SCHEDULED) {
            throw new RuntimeException("Only scheduled trips can be started");
        }

        trip.setStatus(Trip.TripStatus.IN_PROGRESS);
        trip.setStartTime(LocalDateTime.now());

        // Update vehicle and driver status
        Vehicle vehicle = trip.getVehicle();
        vehicle.setStatus(Vehicle.VehicleStatus.IN_USE);
        vehicleRepository.save(vehicle);

        Driver driver = trip.getDriver();
        driver.setStatus(Driver.DriverStatus.ON_TRIP);
        driverRepository.save(driver);

        Trip updatedTrip = tripRepository.save(trip);
        return mapToResponse(updatedTrip);
    }

    // Complete trip
    @Transactional
    public TripResponse completeTrip(Long id, CompleteTripRequest request) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));

        if (trip.getStatus() != Trip.TripStatus.IN_PROGRESS) {
            throw new RuntimeException("Only in-progress trips can be completed");
        }

        // Validate end time is after start time
        if (LocalDateTime.now().isBefore(trip.getStartTime())) {
            throw new RuntimeException("End time cannot be before start time");
        }

        trip.setEndTime(LocalDateTime.now());
        trip.setDistance(request.getDistance());
        trip.setFuelUsed(request.getFuelUsed());
        trip.setStatus(Trip.TripStatus.COMPLETED);

        if (request.getNotes() != null) {
            trip.setNotes(request.getNotes());
        }

        // Update vehicle and driver status
        Vehicle vehicle = trip.getVehicle();
        vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
        vehicleRepository.save(vehicle);

        Driver driver = trip.getDriver();
        driver.setStatus(Driver.DriverStatus.AVAILABLE);
        driver.setTotalTrips(driver.getTotalTrips() + 1);
        driverRepository.save(driver);

        Trip completedTrip = tripRepository.save(trip);
        return mapToResponse(completedTrip);
    }

    // Cancel trip
    @Transactional
    public TripResponse cancelTrip(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));

        if (trip.getStatus() == Trip.TripStatus.COMPLETED) {
            throw new RuntimeException("Completed trips cannot be cancelled");
        }

        trip.setStatus(Trip.TripStatus.CANCELLED);

        // If trip was in progress, update vehicle and driver status
        if (trip.getStatus() == Trip.TripStatus.IN_PROGRESS) {
            Vehicle vehicle = trip.getVehicle();
            vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
            vehicleRepository.save(vehicle);

            Driver driver = trip.getDriver();
            driver.setStatus(Driver.DriverStatus.AVAILABLE);
            driverRepository.save(driver);
        }

        Trip cancelledTrip = tripRepository.save(trip);
        return mapToResponse(cancelledTrip);
    }

    // Delete trip
    public void deleteTrip(Long id) {
        if (!tripRepository.existsById(id)) {
            throw new RuntimeException("Trip not found with id: " + id);
        }
        tripRepository.deleteById(id);
    }

    // Filter by vehicle
    public Page<TripResponse> filterByVehicle(Long vehicleId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Trip> trips = tripRepository.findByVehicleId(vehicleId, pageable);

        return trips.map(this::mapToResponse);
    }

    // Filter by driver
    public Page<TripResponse> filterByDriver(Long driverId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Trip> trips = tripRepository.findByDriverId(driverId, pageable);

        return trips.map(this::mapToResponse);
    }

    // Filter by status
    public Page<TripResponse> filterByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Trip.TripStatus tripStatus = Trip.TripStatus.valueOf(status);
        Page<Trip> trips = tripRepository.findByStatus(tripStatus, pageable);

        return trips.map(this::mapToResponse);
    }

    // Filter by date range
    public Page<TripResponse> filterByDateRange(LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Trip> trips = tripRepository.findTripsByDateRange(startDate, endDate, pageable);

        return trips.map(this::mapToResponse);
    }

    // Map request to entity
    private Trip mapToEntity(TripRequest request, Vehicle vehicle, Driver driver) {
        Trip trip = new Trip();
        trip.setVehicle(vehicle);
        trip.setDriver(driver);
        trip.setStartLocation(request.getStartLocation());
        trip.setEndLocation(request.getEndLocation());
        trip.setStartTime(request.getStartTime());
        trip.setStatus(Trip.TripStatus.valueOf(request.getStatus()));
        trip.setPurpose(request.getPurpose());
        trip.setNotes(request.getNotes());

        return trip;
    }

    // Map entity to response
    private TripResponse mapToResponse(Trip trip) {
        Long durationMinutes = null;
        if (trip.getEndTime() != null) {
            durationMinutes = Duration.between(trip.getStartTime(), trip.getEndTime()).toMinutes();
        }

        return new TripResponse(
                trip.getId(),
                trip.getVehicle().getId(),
                trip.getVehicle().getRegistrationNumber(),
                trip.getVehicle().getBrand(),
                trip.getVehicle().getModel(),
                trip.getDriver().getId(),
                trip.getDriver().getUser().getName(),
                trip.getDriver().getPhoneNumber(),
                trip.getStartLocation(),
                trip.getEndLocation(),
                trip.getStartTime(),
                trip.getEndTime(),
                trip.getDistance(),
                trip.getFuelUsed(),
                trip.getStatus().name(),
                trip.getPurpose(),
                trip.getNotes(),
                durationMinutes,
                trip.getCreatedAt(),
                trip.getUpdatedAt()
        );
    }
}