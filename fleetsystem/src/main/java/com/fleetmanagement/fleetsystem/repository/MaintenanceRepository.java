package com.fleetmanagement.fleetsystem.repository;

import com.fleetmanagement.fleetsystem.model.Maintenance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    Page<Maintenance> findByVehicleId(Long vehicleId, Pageable pageable);

    Page<Maintenance> findByType(Maintenance.MaintenanceType type, Pageable pageable);

    Page<Maintenance> findByStatus(Maintenance.MaintenanceStatus status, Pageable pageable);

    List<Maintenance> findByNextDueDateBeforeAndStatus(LocalDate date, Maintenance.MaintenanceStatus status);

    @Query("SELECT COALESCE(SUM(m.cost), 0) FROM Maintenance m WHERE m.vehicle.id = :vehicleId AND m.status = 'COMPLETED'")
    Double getTotalMaintenanceCostByVehicle(@Param("vehicleId") Long vehicleId);

    @Query("SELECT m FROM Maintenance m WHERE m.maintenanceDate BETWEEN :startDate AND :endDate")
    Page<Maintenance> findByDateRange(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate,
                                      Pageable pageable);

    @Query("SELECT COUNT(m) FROM Maintenance m WHERE m.vehicle.id = :vehicleId AND m.status = 'COMPLETED'")
    Long getMaintenanceCountByVehicle(@Param("vehicleId") Long vehicleId);
}