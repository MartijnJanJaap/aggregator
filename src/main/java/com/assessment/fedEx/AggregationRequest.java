package com.assessment.fedEx;

import java.util.List;

public class AggregationRequest {

    private final List<Double> orderNumbersForTracking;
    private final List<Double> orderNumbersForShipments;
    private final List<String> countryCodesForPricing;

    public AggregationRequest(List<Double> orderNumbersForTracking,
                              List<Double> orderNumbersForShipments,
                              List<String> countryCodesForPricing) {
        this.countryCodesForPricing=countryCodesForPricing;
        this.orderNumbersForTracking=orderNumbersForTracking;
        this.orderNumbersForShipments=orderNumbersForShipments;
    }

    public List<Double> getOrderNumbersForTracking() {
        return orderNumbersForTracking;
    }

    public List<Double> getOrderNumbersForShipments() {
        return orderNumbersForShipments;
    }

    public List<String> getCountryCodesForPricing() {
        return countryCodesForPricing;
    }
}
