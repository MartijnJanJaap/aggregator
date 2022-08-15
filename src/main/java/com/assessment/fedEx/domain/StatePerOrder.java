package com.assessment.fedEx.domain;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record StatePerOrder(Map<String, String> stateForOrders) {

    public StatePerOrder filter(Set<String> requestParams) {
        return new StatePerOrder(stateForOrders.entrySet().stream()
                .filter(entry -> requestParams.stream().anyMatch(param -> param.equals(entry.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }
}
