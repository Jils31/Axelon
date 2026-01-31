package com.fleetmanagement.fleetsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {

    private Long id;
    private Long vehicleId;
    private String vehicleRegistration;
    private String type;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private LocalDate expiryDate;
    private LocalDateTime uploadedAt;
    private String downloadUrl;
}