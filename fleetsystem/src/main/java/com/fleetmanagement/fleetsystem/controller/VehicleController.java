package com.fleetmanagement.fleetsystem.controller;

import com.fleetmanagement.fleetsystem.dto.VehicleRequest;
import com.fleetmanagement.fleetsystem.dto.VehicleResponse;
import com.fleetmanagement.fleetsystem.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Vehicles", description = "Vehicle management APIs")
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @Operation(summary = "Create vehicle", description = "Add a new vehicle to the fleet")
    @PostMapping
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.createVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get vehicle by ID")
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long id) {
        VehicleResponse response = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all vehicles", description = "Get paginated list of all vehicles")
    @GetMapping
    public ResponseEntity<Page<VehicleResponse>> getAllVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Page<VehicleResponse> response = vehicleService.getAllVehicles(page, size, sortBy);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Edit vehicle by ID")
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.updateVehicle(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete the existing vehicle by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get vehicles based on search", description = "Get paginated list of vehicles")
    @GetMapping("/search")
    public ResponseEntity<Page<VehicleResponse>> searchVehicles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<VehicleResponse> response = vehicleService.searchVehicles(keyword, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get vehicles based on it's status", description = "Get paginated list of vehicles")
    @GetMapping("/filter/status")
    public ResponseEntity<Page<VehicleResponse>> filterByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<VehicleResponse> response = vehicleService.filterByStatus(status, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get vehicles based on it's type", description = "Get paginated list of vehicles")
    @GetMapping("/filter/type")
    public ResponseEntity<Page<VehicleResponse>> filterByType(
            @RequestParam String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<VehicleResponse> response = vehicleService.filterByType(type, page, size);
        return ResponseEntity.ok(response);
    }
}