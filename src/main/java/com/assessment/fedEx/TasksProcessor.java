package com.assessment.fedEx;

import com.assessment.fedEx.RequestQueue.API;
import com.assessment.fedEx.RequestQueue.RequestTask;
import com.assessment.fedEx.domain.PricePerCountry;
import com.assessment.fedEx.domain.Request;
import com.assessment.fedEx.domain.ShipmentOptionsPerOrder;
import com.assessment.fedEx.domain.StatePerOrder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.assessment.fedEx.RequestQueue.API.*;
import static java.util.concurrent.CompletableFuture.*;

@Component
public class TasksProcessor implements InitializingBean {

    private final com.assessment.fedEx.XYZClient XYZClient;

    @Autowired
    public TasksProcessor(XYZClient XYZClient) {
        this.XYZClient = XYZClient;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        producerThreadBean();
    }
    public void producerThreadBean(){
        System.out.println("starting producer bean");
        new Thread(() -> {
            System.out.println("Starting producer thread");
            try {
                while (true) {
                    respond();
                    Thread.sleep(100);
                }
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void respond() throws ExecutionException, InterruptedException {
        if(FedExApplication.getQueue().size()==0) {
            return;
        }

        List<Request> requests = new ArrayList<>();
        FedExApplication.getQueue().drainTo(requests);

        Map<API, List<RequestTask>> tasksPerApi = requests.stream()
                .flatMap(request -> request.getRequestTasks().stream())
                .collect(Collectors.groupingBy(RequestTask::api));

        if(tasksPerApi.values().stream().map(List::size).noneMatch(size -> size >= 5) && !requestWaitedLongEnough(tasksPerApi)) {
            FedExApplication.getQueue().addAll(requests);
            System.out.println("not needed to process re");
            return;
        }
        System.out.println("processing " + requests.size() + " requests");

        AggregatedResponses aggregatedResponses = getAggregatedResponses(tasksPerApi);
        requests.forEach( request -> {
            Map<API, List<String>> requestParamsPerApi = getRequestParamsPerApi(request);

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
                .map(dateTime -> ChronoUnit.SECONDS.between(dateTime, LocalDateTime.now()) > 5)
                .orElse(false);
    }

    private Map<API, List<String>> getRequestParamsPerApi(Request request) {
        return request.getRequestTasks().stream()
                .collect(Collectors.groupingBy(RequestTask::api))
                .entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().stream().map(RequestTask::requestParam).toList()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
