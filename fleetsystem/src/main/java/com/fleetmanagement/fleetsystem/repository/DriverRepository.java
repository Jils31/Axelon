package com.fleetmanagement.fleetsystem.repository;

import com.fleetmanagement.fleetsystem.model.Driver;
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
public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByLicenseNumber(String licenseNumber);

    boolean existsByLicenseNumber(String licenseNumber);

    boolean existsByUserId(Long userId);

    Optional<Driver> findByUserId(Long userId);

    Page<Driver> findByStatus(Driver.DriverStatus status, Pageable pageable);

    @Query("SELECT d FROM Driver d WHERE d.status = 'AVAILABLE'")
    List<Driver> findAvailableDrivers();

    List<Driver> findByLicenseExpiryBefore(LocalDate date);

    @Query("SELECT d FROM Driver d WHERE d.rating >= :minRating AND d.rating <= :maxRating")
    Page<Driver> findByRatingRange(@Param("minRating") Double minRating,
                                   @Param("maxRating") Double maxRating,
                                   Pageable pageable);

    @Query("SELECT d FROM Driver d WHERE LOWER(d.user.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR d.phoneNumber LIKE CONCAT('%', :keyword, '%')")
    Page<Driver> searchDrivers(@Param("keyword") String keyword, Pageable pageable);
}