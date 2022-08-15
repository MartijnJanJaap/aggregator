package com.assessment.fedEx.domain;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record PricePerCountry(Map<String, String> pricePerCountry) {

    public PricePerCountry filter(Set<String> requestParams) {
        return new PricePerCountry(pricePerCountry.entrySet().stream()
                .filter(entry -> requestParams.stream().anyMatch(param -> param.equals(entry.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }
}
