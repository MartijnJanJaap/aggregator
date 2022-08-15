package com.assessment.fedEx.domain;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record ShipmentOptionsPerOrder(Map<String, Set<String>> shipmentOptions) {

    public ShipmentOptionsPerOrder filter(Set<String> requestParams) {
        return new ShipmentOptionsPerOrder(shipmentOptions.entrySet().stream()
                .filter(entry -> requestParams.stream().anyMatch(param -> param.equals(entry.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

}
