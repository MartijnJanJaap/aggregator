package com.assessment.fedEx.domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record ShipmentOptionsPerOrder(Map<String, List<String>> shipmentOptions) {

    public ShipmentOptionsPerOrder filter(List<String> requestParams) {
        return new ShipmentOptionsPerOrder(shipmentOptions.entrySet().stream()
                .filter(entry -> requestParams.stream().anyMatch(param -> param.equals(entry.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

}
