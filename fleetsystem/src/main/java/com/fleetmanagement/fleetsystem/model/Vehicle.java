package com.fleetmanagement.fleetsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String registrationNumber;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    @Column(nullable = false)
    private LocalDate purchaseDate;

    private LocalDate lastMaintenanceDate;

    private LocalDate nextMaintenanceDate;

    @Column(nullable = false)
    private Double fuelCapacity;

    @Column(nullable = false)
    private Integer seatingCapacity;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum VehicleType {
        SEDAN,
        SUV,
        TRUCK,
        VAN,
        BUS
    }

    public enum VehicleStatus {
        AVAILABLE,
        IN_USE,
        MAINTENANCE,
        RETIRED
    }
}