package com.fleetmanagement.fleetsystem.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DriverRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "License number is required")
    @Pattern(regexp = "^[A-Z0-9]{10,20}$", message = "Invalid license number format")
    private String licenseNumber;

    @NotNull(message = "License expiry date is required")
    @Future(message = "License expiry must be in future")
    private LocalDate licenseExpiry;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Status is required")
    private String status; // AVAILABLE, ON_TRIP, OFF_DUTY, SUSPENDED
}