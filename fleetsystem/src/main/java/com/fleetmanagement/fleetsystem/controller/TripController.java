package com.fleetmanagement.fleetsystem.controller;

import com.fleetmanagement.fleetsystem.dto.CompleteTripRequest;
import com.fleetmanagement.fleetsystem.dto.TripRequest;
import com.fleetmanagement.fleetsystem.dto.TripResponse;
import com.fleetmanagement.fleetsystem.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody TripRequest request) {
        TripResponse response = tripService.createTrip(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getTripById(@PathVariable Long id) {
        TripResponse response = tripService.getTripById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<TripResponse>> getAllTrips(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Page<TripResponse> response = tripService.getAllTrips(page, size, sortBy);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<TripResponse> startTrip(@PathVariable Long id) {
        TripResponse response = tripService.startTrip(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<TripResponse> completeTrip(
            @PathVariable Long id,
            @Valid @RequestBody CompleteTripRequest request) {
        TripResponse response = tripService.completeTrip(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<TripResponse> cancelTrip(@PathVariable Long id) {
        TripResponse response = tripService.cancelTrip(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        tripService.deleteTrip(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter/vehicle/{vehicleId}")
    public ResponseEntity<Page<TripResponse>> filterByVehicle(
            @PathVariable Long vehicleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TripResponse> response = tripService.filterByVehicle(vehicleId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter/driver/{driverId}")
    public ResponseEntity<Page<TripResponse>> filterByDriver(
            @PathVariable Long driverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TripResponse> response = tripService.filterByDriver(driverId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter/status")
    public ResponseEntity<Page<TripResponse>> filterByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TripResponse> response = tripService.filterByStatus(status, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter/date-range")
    public ResponseEntity<Page<TripResponse>> filterByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TripResponse> response = tripService.filterByDateRange(startDate, endDate, page, size);
        return ResponseEntity.ok(response);
    }
}