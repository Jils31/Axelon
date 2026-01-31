package com.fleetmanagement.fleetsystem.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MaintenanceRequest {

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotBlank(message = "Maintenance type is required")
    private String type; // OIL_CHANGE, TIRE_ROTATION, etc.

    @NotNull(message = "Maintenance date is required")
    private LocalDate maintenanceDate;

    @NotNull(message = "Cost is required")
    @Positive(message = "Cost must be positive")
    private Double cost;

    @NotBlank(message = "Description is required")
    private String description;

    private String serviceProvider;

    @Future(message = "Next due date must be in future")
    private LocalDate nextDueDate;

    @Positive(message = "Next due kilometers must be positive")
    private Integer nextDueKilometers;

    @NotBlank(message = "Status is required")
    private String status; // SCHEDULED, COMPLETED, CANCELLED
}