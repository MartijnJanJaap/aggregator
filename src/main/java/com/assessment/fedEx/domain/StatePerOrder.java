package com.assessment.fedEx.domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record StatePerOrder(Map<String, String> stateForOrders) {

    public StatePerOrder filter(List<String> requestParams) {
        return new StatePerOrder(stateForOrders.entrySet().stream()
                .filter(entry -> requestParams.stream().anyMatch(param -> param.equals(entry.getKey().toString())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }
}
