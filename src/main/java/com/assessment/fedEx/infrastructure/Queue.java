package com.assessment.fedEx.infrastructure;

import com.assessment.fedEx.domain.Request;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class Queue {

    private final LinkedBlockingQueue<Request> queue;

    public Queue() {
        queue = new LinkedBlockingQueue<>();
    }

    public void put(Request request) throws InterruptedException {
        queue.put(request);
    }

    public int size(){
        return queue.size();
    }

    public List<Request> getAll(){
        List<Request> requests = new ArrayList<>();
        queue.drainTo(requests);
        return requests;
    }

    public void addAll(List<Request> requests){
        queue.addAll(requests);
    }
}
