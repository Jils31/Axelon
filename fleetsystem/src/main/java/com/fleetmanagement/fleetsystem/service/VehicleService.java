package com.fleetmanagement.fleetsystem.service;

import com.fleetmanagement.fleetsystem.dto.VehicleRequest;
import com.fleetmanagement.fleetsystem.dto.VehicleResponse;
import com.fleetmanagement.fleetsystem.model.Vehicle;
import com.fleetmanagement.fleetsystem.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    @CacheEvict(value = "vehicles", allEntries = true)
    public VehicleResponse createVehicle(VehicleRequest request) {
        if (vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new RuntimeException("Vehicle with registration number already exists");
        }

        Vehicle vehicle = mapToEntity(request);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return mapToResponse(savedVehicle);
    }

    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));

        return mapToResponse(vehicle);
    }

    @Cacheable(value = "vehicles", key = "#page + '-' + #size + '-' + #sortBy")
    public Page<VehicleResponse> getAllVehicles(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<Vehicle> vehicles = vehicleRepository.findAll(pageable);

        return vehicles.map(this::mapToResponse);
    }

    @CacheEvict(value = "vehicles", allEntries = true)
    public VehicleResponse updateVehicle(Long id, VehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));

        if (!vehicle.getRegistrationNumber().equals(request.getRegistrationNumber()) &&
                vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new RuntimeException("Vehicle with registration number already exists");
        }

        updateEntityFromRequest(vehicle, request);
        Vehicle updatedVehicle = vehicleRepository.save(vehicle);

        return mapToResponse(updatedVehicle);
    }

    @CacheEvict(value = "vehicles", allEntries = true)
    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new RuntimeException("Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    public Page<VehicleResponse> searchVehicles(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> vehicles = vehicleRepository.searchVehicles(keyword, pageable);

        return vehicles.map(this::mapToResponse);
    }

    public Page<VehicleResponse> filterByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Vehicle.VehicleStatus vehicleStatus = Vehicle.VehicleStatus.valueOf(status);
        Page<Vehicle> vehicles = vehicleRepository.findByStatus(vehicleStatus, pageable);

        return vehicles.map(this::mapToResponse);
    }

    public Page<VehicleResponse> filterByType(String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Vehicle.VehicleType vehicleType = Vehicle.VehicleType.valueOf(type);
        Page<Vehicle> vehicles = vehicleRepository.findByType(vehicleType, pageable);

        return vehicles.map(this::mapToResponse);
    }

    private Vehicle mapToEntity(VehicleRequest request) {
        Vehicle vehicle = new Vehicle();
        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setType(Vehicle.VehicleType.valueOf(request.getType()));
        vehicle.setStatus(Vehicle.VehicleStatus.valueOf(request.getStatus()));
        vehicle.setPurchaseDate(request.getPurchaseDate());
        vehicle.setLastMaintenanceDate(request.getLastMaintenanceDate());
        vehicle.setNextMaintenanceDate(request.getNextMaintenanceDate());
        vehicle.setFuelCapacity(request.getFuelCapacity());
        vehicle.setSeatingCapacity(request.getSeatingCapacity());

        return vehicle;
    }

    private void updateEntityFromRequest(Vehicle vehicle, VehicleRequest request) {
        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setType(Vehicle.VehicleType.valueOf(request.getType()));
        vehicle.setStatus(Vehicle.VehicleStatus.valueOf(request.getStatus()));
        vehicle.setPurchaseDate(request.getPurchaseDate());
        vehicle.setLastMaintenanceDate(request.getLastMaintenanceDate());
        vehicle.setNextMaintenanceDate(request.getNextMaintenanceDate());
        vehicle.setFuelCapacity(request.getFuelCapacity());
        vehicle.setSeatingCapacity(request.getSeatingCapacity());
    }

    private VehicleResponse mapToResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getRegistrationNumber(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getType().name(),
                vehicle.getStatus().name(),
                vehicle.getPurchaseDate(),
                vehicle.getLastMaintenanceDate(),
                vehicle.getNextMaintenanceDate(),
                vehicle.getFuelCapacity(),
                vehicle.getSeatingCapacity(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt()
        );
    }
}