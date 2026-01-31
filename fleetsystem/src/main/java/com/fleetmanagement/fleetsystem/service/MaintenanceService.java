package com.fleetmanagement.fleetsystem.service;

import com.fleetmanagement.fleetsystem.dto.MaintenanceRequest;
import com.fleetmanagement.fleetsystem.dto.MaintenanceResponse;
import com.fleetmanagement.fleetsystem.model.Maintenance;
import com.fleetmanagement.fleetsystem.model.Vehicle;
import com.fleetmanagement.fleetsystem.repository.MaintenanceRepository;
import com.fleetmanagement.fleetsystem.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;
    private final EmailService emailService;

    @Transactional
    public MaintenanceResponse createMaintenance(MaintenanceRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + request.getVehicleId()));

        Maintenance maintenance = mapToEntity(request, vehicle);
        Maintenance savedMaintenance = maintenanceRepository.save(maintenance);

        // Update vehicle maintenance dates if completed
        if (request.getStatus().equals("COMPLETED")) {
            vehicle.setLastMaintenanceDate(request.getMaintenanceDate());
            if (request.getNextDueDate() != null) {
                vehicle.setNextMaintenanceDate(request.getNextDueDate());
            }
            vehicleRepository.save(vehicle);

            // Send completion email
            emailService.sendMaintenanceCompletionNotification(
                    "spidy1800@gmail.com",
                    vehicle.getRegistrationNumber(),
                    request.getType(),
                    request.getCost()
            );
        }

        return mapToResponse(savedMaintenance);
    }

    public MaintenanceResponse getMaintenanceById(Long id) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance record not found with id: " + id));

        return mapToResponse(maintenance);
    }

    public Page<MaintenanceResponse> getAllMaintenance(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<Maintenance> maintenance = maintenanceRepository.findAll(pageable);

        return maintenance.map(this::mapToResponse);
    }

    @Transactional
    public MaintenanceResponse updateMaintenance(Long id, MaintenanceRequest request) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance record not found with id: " + id));

        updateEntityFromRequest(maintenance, request);
        Maintenance updatedMaintenance = maintenanceRepository.save(maintenance);

        // Update vehicle dates if completed
        if (request.getStatus().equals("COMPLETED")) {
            Vehicle vehicle = maintenance.getVehicle();
            vehicle.setLastMaintenanceDate(request.getMaintenanceDate());
            if (request.getNextDueDate() != null) {
                vehicle.setNextMaintenanceDate(request.getNextDueDate());
            }
            vehicleRepository.save(vehicle);
        }

        return mapToResponse(updatedMaintenance);
    }

    public void deleteMaintenance(Long id) {
        if (!maintenanceRepository.existsById(id)) {
            throw new RuntimeException("Maintenance record not found with id: " + id);
        }
        maintenanceRepository.deleteById(id);
    }

    public Page<MaintenanceResponse> filterByVehicle(Long vehicleId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Maintenance> maintenance = maintenanceRepository.findByVehicleId(vehicleId, pageable);

        return maintenance.map(this::mapToResponse);
    }

    public Page<MaintenanceResponse> filterByType(String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Maintenance.MaintenanceType maintenanceType = Maintenance.MaintenanceType.valueOf(type);
        Page<Maintenance> maintenance = maintenanceRepository.findByType(maintenanceType, pageable);

        return maintenance.map(this::mapToResponse);
    }

    public Page<MaintenanceResponse> filterByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Maintenance.MaintenanceStatus maintenanceStatus = Maintenance.MaintenanceStatus.valueOf(status);
        Page<Maintenance> maintenance = maintenanceRepository.findByStatus(maintenanceStatus, pageable);

        return maintenance.map(this::mapToResponse);
    }

    public Page<MaintenanceResponse> filterByDateRange(LocalDate startDate, LocalDate endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Maintenance> maintenance = maintenanceRepository.findByDateRange(startDate, endDate, pageable);

        return maintenance.map(this::mapToResponse);
    }

    // Send alerts for upcoming maintenance
    public void sendMaintenanceAlerts() {
        LocalDate alertDate = LocalDate.now().plusDays(7); // Alert 7 days before
        var dueMaintenance = maintenanceRepository.findByNextDueDateBeforeAndStatus(
                alertDate,
                Maintenance.MaintenanceStatus.SCHEDULED
        );

        for (Maintenance maintenance : dueMaintenance) {
            emailService.sendMaintenanceAlert(
                    "spidy1800@gmail.com",
                    maintenance.getVehicle().getRegistrationNumber(),
                    maintenance.getType().name(),
                    maintenance.getNextDueDate().toString()
            );
        }
    }

    private Maintenance mapToEntity(MaintenanceRequest request, Vehicle vehicle) {
        Maintenance maintenance = new Maintenance();
        maintenance.setVehicle(vehicle);
        maintenance.setType(Maintenance.MaintenanceType.valueOf(request.getType()));
        maintenance.setMaintenanceDate(request.getMaintenanceDate());
        maintenance.setCost(request.getCost());
        maintenance.setDescription(request.getDescription());
        maintenance.setServiceProvider(request.getServiceProvider());
        maintenance.setNextDueDate(request.getNextDueDate());
        maintenance.setNextDueKilometers(request.getNextDueKilometers());
        maintenance.setStatus(Maintenance.MaintenanceStatus.valueOf(request.getStatus()));

        return maintenance;
    }

    private void updateEntityFromRequest(Maintenance maintenance, MaintenanceRequest request) {
        maintenance.setType(Maintenance.MaintenanceType.valueOf(request.getType()));
        maintenance.setMaintenanceDate(request.getMaintenanceDate());
        maintenance.setCost(request.getCost());
        maintenance.setDescription(request.getDescription());
        maintenance.setServiceProvider(request.getServiceProvider());
        maintenance.setNextDueDate(request.getNextDueDate());
        maintenance.setNextDueKilometers(request.getNextDueKilometers());
        maintenance.setStatus(Maintenance.MaintenanceStatus.valueOf(request.getStatus()));
    }

    private MaintenanceResponse mapToResponse(Maintenance maintenance) {
        return new MaintenanceResponse(
                maintenance.getId(),
                maintenance.getVehicle().getId(),
                maintenance.getVehicle().getRegistrationNumber(),
                maintenance.getVehicle().getBrand(),
                maintenance.getVehicle().getModel(),
                maintenance.getType().name(),
                maintenance.getMaintenanceDate(),
                maintenance.getCost(),
                maintenance.getDescription(),
                maintenance.getServiceProvider(),
                maintenance.getNextDueDate(),
                maintenance.getNextDueKilometers(),
                maintenance.getStatus().name(),
                maintenance.getCreatedAt(),
                maintenance.getUpdatedAt()
        );
    }
}