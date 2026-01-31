package com.fleetmanagement.fleetsystem.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {

    private final WebClient.Builder webClientBuilder;

    @Value("${exchange.api.url}")
    private String apiUrl;

    @Cacheable(value = "currencyRates", key = "#baseCurrency")
    public Map<String, Double> getExchangeRates(String baseCurrency) {
        try {
            String response = webClientBuilder.build()
                    .get()
                    .uri(apiUrl.replace("USD", baseCurrency))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode rates = root.get("rates");

            return mapper.convertValue(rates, Map.class);
        } catch (Exception e) {
            log.error("Failed to fetch exchange rates", e);
            throw new RuntimeException("Failed to fetch exchange rates: " + e.getMessage());
        }
    }

    public Double convertCurrency(Double amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        Map<String, Double> rates = getExchangeRates(fromCurrency);

        if (!rates.containsKey(toCurrency)) {
            throw new RuntimeException("Unsupported currency: " + toCurrency);
        }

        Double rate = rates.get(toCurrency);
        return amount * rate;
    }

    public Double getExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return 1.0;
        }

        Map<String, Double> rates = getExchangeRates(fromCurrency);

        if (!rates.containsKey(toCurrency)) {
            throw new RuntimeException("Unsupported currency: " + toCurrency);
        }

        return rates.get(toCurrency);
    }
}