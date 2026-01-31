package com.fleetmanagement.fleetsystem.service;

import com.fleetmanagement.fleetsystem.dto.DriverRequest;
import com.fleetmanagement.fleetsystem.dto.DriverResponse;
import com.fleetmanagement.fleetsystem.model.Driver;
import com.fleetmanagement.fleetsystem.model.User;
import com.fleetmanagement.fleetsystem.repository.DriverRepository;
import com.fleetmanagement.fleetsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    @CacheEvict(value = "drivers", allEntries = true)
    public DriverResponse createDriver(DriverRequest request) {
        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new RuntimeException("Driver with license number already exists");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        if (driverRepository.existsByUserId(request.getUserId())) {
            throw new RuntimeException("User is already registered as a driver");
        }

        int age = Period.between(request.getDateOfBirth(), LocalDate.now()).getYears();
        if (age < 18) {
            throw new RuntimeException("Driver must be at least 18 years old");
        }

        Driver driver = mapToEntity(request, user);
        Driver savedDriver = driverRepository.save(driver);

        return mapToResponse(savedDriver);
    }

    public DriverResponse getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));

        return mapToResponse(driver);
    }

    @Cacheable(value = "drivers", key = "#page + '-' + #size + '-' + #sortBy")
    public Page<DriverResponse> getAllDrivers(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<Driver> drivers = driverRepository.findAll(pageable);

        return drivers.map(this::mapToResponse);
    }

    @CacheEvict(value = "drivers", allEntries = true)
    public DriverResponse updateDriver(Long id, DriverRequest request) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));

        if (!driver.getLicenseNumber().equals(request.getLicenseNumber()) &&
                driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new RuntimeException("Driver with license number already exists");
        }

        int age = Period.between(request.getDateOfBirth(), LocalDate.now()).getYears();
        if (age < 18) {
            throw new RuntimeException("Driver must be at least 18 years old");
        }

        updateEntityFromRequest(driver, request);
        Driver updatedDriver = driverRepository.save(driver);

        return mapToResponse(updatedDriver);
    }

    @CacheEvict(value = "drivers", allEntries = true)
    public void deleteDriver(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new RuntimeException("Driver not found with id: " + id);
        }
        driverRepository.deleteById(id);
    }

    public Page<DriverResponse> searchDrivers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Driver> drivers = driverRepository.searchDrivers(keyword, pageable);

        return drivers.map(this::mapToResponse);
    }

    public Page<DriverResponse> filterByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Driver.DriverStatus driverStatus = Driver.DriverStatus.valueOf(status);
        Page<Driver> drivers = driverRepository.findByStatus(driverStatus, pageable);

        return drivers.map(this::mapToResponse);
    }

    public Page<DriverResponse> filterByRating(Double minRating, Double maxRating, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Driver> drivers = driverRepository.findByRatingRange(minRating, maxRating, pageable);

        return drivers.map(this::mapToResponse);
    }

    private Driver mapToEntity(DriverRequest request, User user) {
        Driver driver = new Driver();
        driver.setUser(user);
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setLicenseExpiry(request.getLicenseExpiry());
        driver.setPhoneNumber(request.getPhoneNumber());
        driver.setAddress(request.getAddress());
        driver.setDateOfBirth(request.getDateOfBirth());
        driver.setStatus(Driver.DriverStatus.valueOf(request.getStatus()));

        return driver;
    }

    private void updateEntityFromRequest(Driver driver, DriverRequest request) {
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setLicenseExpiry(request.getLicenseExpiry());
        driver.setPhoneNumber(request.getPhoneNumber());
        driver.setAddress(request.getAddress());
        driver.setDateOfBirth(request.getDateOfBirth());
        driver.setStatus(Driver.DriverStatus.valueOf(request.getStatus()));
    }

    private DriverResponse mapToResponse(Driver driver) {
        return new DriverResponse(
                driver.getId(),
                driver.getUser().getId(),
                driver.getUser().getName(),
                driver.getUser().getEmail(),
                driver.getLicenseNumber(),
                driver.getLicenseExpiry(),
                driver.getPhoneNumber(),
                driver.getAddress(),
                driver.getDateOfBirth(),
                driver.getStatus().name(),
                driver.getRating(),
                driver.getTotalTrips(),
                driver.getCreatedAt(),
                driver.getUpdatedAt()
        );
    }
}