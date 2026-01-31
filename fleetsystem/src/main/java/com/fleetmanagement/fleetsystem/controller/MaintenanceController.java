package com.fleetmanagement.fleetsystem.controller;

import com.fleetmanagement.fleetsystem.dto.MaintenanceRequest;
import com.fleetmanagement.fleetsystem.dto.MaintenanceResponse;
import com.fleetmanagement.fleetsystem.service.MaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping
    public ResponseEntity<MaintenanceResponse> createMaintenance(@Valid @RequestBody MaintenanceRequest request) {
        MaintenanceResponse response = maintenanceService.createMaintenance(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceResponse> getMaintenanceById(@PathVariable Long id) {
        MaintenanceResponse response = maintenanceService.getMaintenanceById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<MaintenanceResponse>> getAllMaintenance(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Page<MaintenanceResponse> response = maintenanceService.getAllMaintenance(page, size, sortBy);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceResponse> updateMaintenance(
            @PathVariable Long id,
            @Valid @RequestBody MaintenanceRequest request) {
        MaintenanceResponse response = maintenanceService.updateMaintenance(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaintenance(@PathVariable Long id) {
        maintenanceService.deleteMaintenance(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter/vehicle/{vehicleId}")
    public ResponseEntity<Page<MaintenanceResponse>> filterByVehicle(
            @PathVariable Long vehicleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MaintenanceResponse> response = maintenanceService.filterByVehicle(vehicleId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter/type")
    public ResponseEntity<Page<MaintenanceResponse>> filterByType(
            @RequestParam String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MaintenanceResponse> response = maintenanceService.filterByType(type, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter/status")
    public ResponseEntity<Page<MaintenanceResponse>> filterByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MaintenanceResponse> response = maintenanceService.filterByStatus(status, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter/date-range")
    public ResponseEntity<Page<MaintenanceResponse>> filterByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MaintenanceResponse> response = maintenanceService.filterByDateRange(startDate, endDate, page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-alerts")
    public ResponseEntity<String> sendMaintenanceAlerts() {
        maintenanceService.sendMaintenanceAlerts();
        return ResponseEntity.ok("Maintenance alerts sent successfully");
    }
}