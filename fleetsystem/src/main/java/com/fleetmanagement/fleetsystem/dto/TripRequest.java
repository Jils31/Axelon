package com.fleetmanagement.fleetsystem.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TripRequest {

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotNull(message = "Driver ID is required")
    private Long driverId;

    @NotBlank(message = "Start location is required")
    private String startLocation;

    @NotBlank(message = "End location is required")
    private String endLocation;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in future")
    private LocalDateTime startTime;

    @NotBlank(message = "Status is required")
    private String status; // SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED

    private String purpose;

    private String notes;
}