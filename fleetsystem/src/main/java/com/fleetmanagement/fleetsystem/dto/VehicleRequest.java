package com.fleetmanagement.fleetsystem.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VehicleRequest {

    @NotBlank(message = "Registration number is required")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Invalid registration number format")
    private String registrationNumber;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Vehicle type is required")
    private String type;

    @NotBlank(message = "Status is required")
    private String status;

    @NotNull(message = "Purchase date is required")
    @PastOrPresent(message = "Purchase date cannot be in future")
    private LocalDate purchaseDate;

    @PastOrPresent(message = "Last maintenance date cannot be in future")
    private LocalDate lastMaintenanceDate;

    @Future(message = "Next maintenance date must be in future")
    private LocalDate nextMaintenanceDate;

    @NotNull(message = "Fuel capacity is required")
    @Positive(message = "Fuel capacity must be positive")
    private Double fuelCapacity;

    @NotNull(message = "Seating capacity is required")
    @Min(value = 1, message = "Seating capacity must be at least 1")
    private Integer seatingCapacity;
}