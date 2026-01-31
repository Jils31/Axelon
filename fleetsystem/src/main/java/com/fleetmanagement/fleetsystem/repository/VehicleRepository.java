package com.fleetmanagement.fleetsystem.repository;

import com.fleetmanagement.fleetsystem.model.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

    boolean existsByRegistrationNumber(String registrationNumber);

    Page<Vehicle> findByStatus(Vehicle.VehicleStatus status, Pageable pageable);

    Page<Vehicle> findByType(Vehicle.VehicleType type, Pageable pageable);

    List<Vehicle> findByNextMaintenanceDateBefore(LocalDate date);

    @Query("SELECT v FROM Vehicle v WHERE v.status = 'AVAILABLE'")
    List<Vehicle> findAvailableVehicles();

    @Query("SELECT v FROM Vehicle v WHERE LOWER(v.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(v.model) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Vehicle> searchVehicles(@Param("keyword") String keyword, Pageable pageable);
}