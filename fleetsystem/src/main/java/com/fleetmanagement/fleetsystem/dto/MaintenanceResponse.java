package com.fleetmanagement.fleetsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceResponse {

    private Long id;
    private Long vehicleId;
    private String vehicleRegistration;
    private String vehicleBrand;
    private String vehicleModel;
    private String type;
    private LocalDate maintenanceDate;
    private Double cost;
    private String description;
    private String serviceProvider;
    private LocalDate nextDueDate;
    private Integer nextDueKilometers;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}