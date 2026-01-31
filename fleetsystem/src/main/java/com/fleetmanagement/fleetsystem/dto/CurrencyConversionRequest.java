package com.fleetmanagement.fleetsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CurrencyConversionRequest {

    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "From currency is required")
    private String fromCurrency;

    @NotBlank(message = "To currency is required")
    private String toCurrency;
}