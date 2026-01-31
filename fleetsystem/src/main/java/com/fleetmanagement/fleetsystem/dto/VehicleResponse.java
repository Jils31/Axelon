package com.fleetmanagement.fleetsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {

    private Long id;
    private String registrationNumber;
    private String brand;
    private String model;
    private String type;
    private String status;
    private LocalDate purchaseDate;
    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDate;
    private Double fuelCapacity;
    private Integer seatingCapacity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}