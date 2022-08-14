package com.assessment.fedEx.domain;

import org.springframework.web.context.request.async.DeferredResult;

import java.util.Set;

public class Request {

    private final DeferredResult<AggregatedResponses> deferredResult;
    private final Set<RequestTask> requestTasks;

    public Request(DeferredResult<AggregatedResponses> deferredResult, Set<RequestTask> requestTask) {
        this.deferredResult = deferredResult;
        this.requestTasks = requestTask;
    }

    public Set<RequestTask> getRequestTasks() {
        return requestTasks;
    }

    public DeferredResult<AggregatedResponses> getDeferredResult() {
        return deferredResult;
    }
}
