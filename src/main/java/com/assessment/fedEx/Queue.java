package com.assessment.fedEx;

import com.assessment.fedEx.domain.Request;
import org.springframework.stereotype.Component;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class Queue {

    private final LinkedBlockingQueue<Request> queue;

    public Queue() {
        queue = new LinkedBlockingQueue<>();
    }

    public LinkedBlockingQueue<Request> getQueue() {
        return queue;
    }
}
