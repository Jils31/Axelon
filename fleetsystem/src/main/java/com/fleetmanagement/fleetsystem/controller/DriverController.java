package com.fleetmanagement.fleetsystem.controller;

import com.fleetmanagement.fleetsystem.dto.DriverRequest;
import com.fleetmanagement.fleetsystem.dto.DriverResponse;
import com.fleetmanagement.fleetsystem.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    public ResponseEntity<DriverResponse> createDriver(@Valid @RequestBody DriverRequest request) {
        DriverResponse response = driverService.createDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> getDriverById(@PathVariable Long id) {
        DriverResponse response = driverService.getDriverById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<DriverResponse>> getAllDrivers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Page<DriverResponse> response = driverService.getAllDrivers(page, size, sortBy);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponse> updateDriver(
            @PathVariable Long id,
            @Valid @RequestBody DriverRequest request) {
        DriverResponse response = driverService.updateDriver(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<DriverResponse>> searchDrivers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<DriverResponse> response = driverService.searchDrivers(keyword, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter/status")
    public ResponseEntity<Page<DriverResponse>> filterByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<DriverResponse> response = driverService.filterByStatus(status, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter/rating")
    public ResponseEntity<Page<DriverResponse>> filterByRating(
            @RequestParam Double minRating,
            @RequestParam Double maxRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<DriverResponse> response = driverService.filterByRating(minRating, maxRating, page, size);
        return ResponseEntity.ok(response);
    }
}