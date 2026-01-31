package com.fleetmanagement.fleetsystem.repository;

import com.fleetmanagement.fleetsystem.model.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    Page<Document> findByVehicleId(Long vehicleId, Pageable pageable);

    Page<Document> findByType(Document.DocumentType type, Pageable pageable);

    List<Document> findByExpiryDateBefore(LocalDate date);

    @Query("SELECT d FROM Document d WHERE d.vehicle.id = :vehicleId AND d.type = :type")
    List<Document> findByVehicleAndType(@Param("vehicleId") Long vehicleId,
                                        @Param("type") Document.DocumentType type);
}