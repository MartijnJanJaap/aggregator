package com.assessment.fedEx;

import com.assessment.fedEx.domain.AggregatedResponses;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.SynchronousQueue;

public class Queue {

    private SynchronousQueue<DeferredResult<AggregatedResponses>> queue;

    public Queue(SynchronousQueue<DeferredResult<AggregatedResponses>> queue) {
        this.queue = queue;
    }
}
