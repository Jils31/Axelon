package com.fleetmanagement.fleetsystem.service;

import com.fleetmanagement.fleetsystem.dto.DocumentResponse;
import com.fleetmanagement.fleetsystem.model.Document;
import com.fleetmanagement.fleetsystem.model.Vehicle;
import com.fleetmanagement.fleetsystem.repository.DocumentRepository;
import com.fleetmanagement.fleetsystem.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final VehicleRepository vehicleRepository;
    private final FileStorageService fileStorageService;

    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "application/pdf",
            "image/jpeg",
            "image/jpg",
            "image/png"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Transactional
    public DocumentResponse uploadDocument(Long vehicleId, String documentType,
                                           LocalDate expiryDate, MultipartFile file) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + vehicleId));

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds 5MB limit");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("Only PDF and image files (JPEG, PNG) are allowed");
        }

        String fileName = fileStorageService.storeFile(file);

        Document document = new Document();
        document.setVehicle(vehicle);
        document.setType(Document.DocumentType.valueOf(documentType));
        document.setFileName(file.getOriginalFilename());
        document.setFilePath(fileName);
        document.setFileSize(file.getSize());
        document.setFileType(file.getContentType());
        document.setExpiryDate(expiryDate);

        Document savedDocument = documentRepository.save(document);
        return mapToResponse(savedDocument);
    }

    public DocumentResponse getDocumentById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));

        return mapToResponse(document);
    }

    public Page<DocumentResponse> getAllDocuments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Document> documents = documentRepository.findAll(pageable);

        return documents.map(this::mapToResponse);
    }

    public Page<DocumentResponse> filterByVehicle(Long vehicleId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Document> documents = documentRepository.findByVehicleId(vehicleId, pageable);

        return documents.map(this::mapToResponse);
    }

    public Page<DocumentResponse> filterByType(String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Document.DocumentType docType = Document.DocumentType.valueOf(type);
        Page<Document> documents = documentRepository.findByType(docType, pageable);

        return documents.map(this::mapToResponse);
    }

    public Resource downloadDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));

        return fileStorageService.loadFileAsResource(document.getFilePath());
    }

    @Transactional
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));

        fileStorageService.deleteFile(document.getFilePath());
        documentRepository.deleteById(id);
    }

    private DocumentResponse mapToResponse(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getVehicle().getId(),
                document.getVehicle().getRegistrationNumber(),
                document.getType().name(),
                document.getFileName(),
                document.getFileSize(),
                document.getFileType(),
                document.getExpiryDate(),
                document.getUploadedAt(),
                "/api/documents/" + document.getId() + "/download"
        );
    }
}