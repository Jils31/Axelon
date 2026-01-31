package com.fleetmanagement.fleetsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyConversionResponse {
    private Double amount;
    private String fromCurrency;
    private String toCurrency;
    private Double convertedAmount;
    private Double exchangeRate;
}