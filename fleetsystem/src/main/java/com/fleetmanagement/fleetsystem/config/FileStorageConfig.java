package com.fleetmanagement.fleetsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig {

    private final String uploadDir = "uploads/documents";

    @Bean
    public Path fileStorageLocation() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }
}