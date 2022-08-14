package com.assessment.fedEx.domain;

import com.assessment.fedEx.domain.PricePerCountry;
import com.assessment.fedEx.domain.ShipmentOptionsPerOrder;
import com.assessment.fedEx.domain.StatePerOrder;

import java.util.Optional;

public class AggregatedResponses {

    private final PricePerCountry pricing;

    private final StatePerOrder statePerOrder;

    private final ShipmentOptionsPerOrder shipmentOptionsPerOrder;

    public AggregatedResponses(ShipmentOptionsPerOrder shipmentOptionsPerOrder,
                               StatePerOrder statePerOrder,
                               PricePerCountry pricing) {
        this.pricing = pricing;
        this.statePerOrder = statePerOrder;
        this.shipmentOptionsPerOrder = shipmentOptionsPerOrder;
    }

    public Optional<PricePerCountry> getPricing() {
        return Optional.ofNullable(pricing);
    }

    public Optional<StatePerOrder> getStatePerOrder() {
        return Optional.ofNullable(statePerOrder);
    }

    public Optional<ShipmentOptionsPerOrder> getShipmentOptionsPerOrder() {
        return Optional.ofNullable(shipmentOptionsPerOrder);
    }
}
