package com.assessment.fedEx;

import com.assessment.fedEx.domain.PricePerCountry;
import com.assessment.fedEx.domain.ShipmentOptionsPerOrder;
import com.assessment.fedEx.domain.StatePerOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class XYZClient {

        private final WebClient webClient;

        @Autowired
        public XYZClient() {
                this.webClient = WebClient.builder()
                        .baseUrl("http://localhost:8080")
                        .build();
        }

        public ShipmentOptionsPerOrder getShipments(String query) {
                System.out.println("fetching shipments for "+ query);
                return new ShipmentOptionsPerOrder(this.webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/shipments")
                                .queryParam("q", query)
                                .build())
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, List<String>>>() {})
                        .block());
        }

        public StatePerOrder getStates(String orderNumbers) {
                System.out.println("fetching states for " + orderNumbers);
                return new StatePerOrder(this.webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/track")
                                .queryParam("q", orderNumbers)
                                .build())
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                        .block());
        }

        public PricePerCountry getPricing(String countryCodes) {
                System.out.println("fetching pricing for " + countryCodes);
                return new PricePerCountry(this.webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/pricing")
                                .queryParam("q", countryCodes)
                                .build())
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                        .block());
        }

}
