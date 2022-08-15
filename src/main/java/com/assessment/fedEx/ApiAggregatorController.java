package com.assessment.fedEx;

import com.assessment.fedEx.domain.AggregatedResponses;
import com.assessment.fedEx.domain.AggregationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class ApiAggregatorController {

    private AggregationService aggregationService;

    @Autowired
    public ApiAggregatorController(AggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    @GetMapping("/aggregation")
    public DeferredResult<AggregatedResponses> getAggregated(
            @RequestParam(required = false, defaultValue = "") List<String> pricing,
            @RequestParam(required = false, defaultValue = "") List<Double> track,
            @RequestParam(required = false, defaultValue = "") List<Double> shipments
    ) throws ExecutionException, InterruptedException {
        DeferredResult<AggregatedResponses> deferredResult = new DeferredResult<>();
        aggregationService.createAggregateRequestsTask(new AggregationRequest(track, shipments, pricing), deferredResult);
        return deferredResult;
    }
}
