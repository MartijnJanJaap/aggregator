package com.assessment.fedEx;

import com.assessment.fedEx.domain.RequestTask;
import com.assessment.fedEx.domain.*;
import com.assessment.fedEx.infrastructure.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.assessment.fedEx.domain.API.*;
import static java.util.concurrent.CompletableFuture.*;

@Component
public class TasksProcessor {

    private final XYZClient XYZClient;
    private final com.assessment.fedEx.infrastructure.Queue queue;

    @Autowired
    public TasksProcessor(XYZClient XYZClient, Queue queue) {
        this.XYZClient = XYZClient;
        this.queue = queue;
    }

    public void process() throws ExecutionException, InterruptedException {
        if(queue.size()==0) {
            return;
        }

        List<Request> requests = queue.getAll();

        Map<API, List<RequestTask>> tasksPerApi = requests.stream()
                .flatMap(request -> request.getRequestTasks().stream())
                .collect(Collectors.groupingBy(RequestTask::api));

        if(tasksPerApi.values().stream().map(List::size).noneMatch(size -> size >= 5) && !requestWaitedLongEnough(tasksPerApi)) {
            queue.addAll(requests);
            return;
        }

        System.out.println("processing " + requests.size() + " requests");

        AggregatedResponses aggregatedResponses = getAggregatedResponses(tasksPerApi);
        requests.forEach( request -> {
            Map<API, Set<String>> requestParamsPerApi = getRequestParamsPerApi(request);

            boolean responded = request.getDeferredResult().setResult(
                    new AggregatedResponses(
                            aggregatedResponses.getShipmentOptionsPerOrder()
                                    .map(aggregated -> aggregated.filter(requestParamsPerApi.get(SHIPMENTS)))
                                    .orElse(null),
                            aggregatedResponses.getStatePerOrder()
                                    .map(aggregated -> aggregated.filter(requestParamsPerApi.get(TRACK)))
                                    .orElse(null),
                            aggregatedResponses.getPricing()
                                    .map(aggregated -> aggregated.filter(requestParamsPerApi.get(PRICING)))
                                    .orElse(null)
                    )
            );
            if(!responded){
                System.out.println("Failed to respond to request");
            }
        });
    }

    private AggregatedResponses getAggregatedResponses(Map<API, List<RequestTask>> tasksPerApi) {

        List<CompletableFuture<? extends Record>> completableFutures = tasksPerApi.entrySet().stream().map(entry -> {
            String request = entry.getValue().stream()
                    .map(RequestTask::requestParam)
                    .collect(Collectors.joining(","));
            return switch (entry.getKey()) {
                case SHIPMENTS -> supplyAsync(() -> XYZClient.getShipments(request));
                case TRACK -> supplyAsync(() -> XYZClient.getStates(request));
                case PRICING -> supplyAsync(() -> XYZClient.getPricing(request));
            };
        }).toList();

        Map<Class<?>, Record> allResponses = new HashMap<>();
        completableFutures.forEach(completableFuture -> {
            try {
                Record record = completableFuture.join();
                if(record!=null) {
                    allResponses.put(record.getClass(), record);
                }
            } catch (CancellationException | CompletionException ex) {
                System.out.println(ex.getMessage());
            }
        });

        return new AggregatedResponses(
                (ShipmentOptionsPerOrder) allResponses.get(ShipmentOptionsPerOrder.class),
                (StatePerOrder) allResponses.get(StatePerOrder.class),
                (PricePerCountry) allResponses.get(PricePerCountry.class)
        );
    }

    private boolean requestWaitedLongEnough(Map<API, List<RequestTask>> tasksPerApi) {
        return tasksPerApi.values().stream()
                .flatMap(List::stream)
                .map(RequestTask::dateTime)
                .sorted()
                .findFirst()
                .map(dateTime -> ChronoUnit.SECONDS.between(dateTime, LocalDateTime.now()) > 4)
                .orElse(false);
    }

    private Map<API, Set<String>> getRequestParamsPerApi(Request request) {
        return request.getRequestTasks().stream()
                .collect(Collectors.groupingBy(RequestTask::api))
                .entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().stream().map(RequestTask::requestParam).collect(Collectors.toSet())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
