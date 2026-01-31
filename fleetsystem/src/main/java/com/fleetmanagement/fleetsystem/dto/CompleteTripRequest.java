package com.fleetmanagement.fleetsystem.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompleteTripRequest {


    @NotNull(message = "Distance is required")
    @Positive(message = "Distance must be positive")
    private Double distance;

    @NotNull(message = "Fuel used is required")
    @Positive(message = "Fuel used must be positive")
    private Double fuelUsed;

    private String notes;
}