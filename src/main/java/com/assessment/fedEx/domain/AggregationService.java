package com.assessment.fedEx.domain;

import com.assessment.fedEx.AggregatedResponses;
import com.assessment.fedEx.AggregationRequest;
import com.assessment.fedEx.FedExApplication;
import com.assessment.fedEx.RequestQueue.API;
import com.assessment.fedEx.RequestQueue.RequestTask;
import com.assessment.fedEx.XYZClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SourceFilteringListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.assessment.fedEx.RequestQueue.API.*;

@Component
public class AggregationService {

    public void createAggregateRequestsTask(AggregationRequest request, DeferredResult<AggregatedResponses> deferredResult) throws ExecutionException, InterruptedException {
        FedExApplication.getQueue().put(new Request(deferredResult, getRequestTasks(request)));
    }

    private Set<RequestTask> getRequestTasks(AggregationRequest request) {
        Set<RequestTask> requestTasks = getRequestTask(SHIPMENTS, request.getOrderNumbersForShipments());
        requestTasks.addAll(getRequestTask(TRACK, request.getOrderNumbersForTracking()));
        requestTasks.addAll(getPricingTask(request.getCountryCodesForPricing()));
        return requestTasks;
    }

    private Set<RequestTask> getRequestTask(API api, List<Double> params) {
        return params.stream().map(param ->
                new RequestTask(
                    api,
                    param.toString().split("\\.")[0],
                    LocalDateTime.now())
        ).collect(Collectors.toSet());
    }

    //think of way to remove duplicate code
    private Set<RequestTask> getPricingTask(List<String> params) {
        return params.stream().map(param ->
                new RequestTask(
                        PRICING,
                        param,
                        LocalDateTime.now())
        ).collect(Collectors.toSet());
    }

}
