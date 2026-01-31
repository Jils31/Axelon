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
@Table(name = "maintenance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceType type;

    @Column(nullable = false)
    private LocalDate maintenanceDate;

    @Column(nullable = false)
    private Double cost;

    @Column(nullable = false)
    private String description;

    private String serviceProvider;

    private LocalDate nextDueDate;

    private Integer nextDueKilometers;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum MaintenanceType {
        OIL_CHANGE,
        TIRE_ROTATION,
        BRAKE_SERVICE,
        ENGINE_TUNE_UP,
        TRANSMISSION_SERVICE,
        BATTERY_REPLACEMENT,
        GENERAL_INSPECTION,
        REPAIR,
        OTHER
    }

    public enum MaintenanceStatus {
        SCHEDULED,
        COMPLETED,
        CANCELLED
    }
}