package com.fleetmanagement.fleetsystem.controller;

import com.fleetmanagement.fleetsystem.dto.CurrencyConversionRequest;
import com.fleetmanagement.fleetsystem.dto.CurrencyConversionResponse;
import com.fleetmanagement.fleetsystem.service.CurrencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/currency")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @PostMapping("/convert")
    public ResponseEntity<CurrencyConversionResponse> convertCurrency(
            @Valid @RequestBody CurrencyConversionRequest request) {

        Double convertedAmount = currencyService.convertCurrency(
                request.getAmount(),
                request.getFromCurrency(),
                request.getToCurrency()
        );

        Double exchangeRate = currencyService.getExchangeRate(
                request.getFromCurrency(),
                request.getToCurrency()
        );

        CurrencyConversionResponse response = new CurrencyConversionResponse(
                request.getAmount(),
                request.getFromCurrency(),
                request.getToCurrency(),
                convertedAmount,
                exchangeRate
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/rates/{baseCurrency}")
    public ResponseEntity<Map<String, Double>> getExchangeRates(@PathVariable String baseCurrency) {
        Map<String, Double> rates = currencyService.getExchangeRates(baseCurrency);
        return ResponseEntity.ok(rates);
    }
}