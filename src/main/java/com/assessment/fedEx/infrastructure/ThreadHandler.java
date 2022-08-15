package com.assessment.fedEx.infrastructure;

import com.assessment.fedEx.TasksProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class ThreadHandler implements InitializingBean {

    private final TasksProcessor tasksProcessor;

    public ThreadHandler(TasksProcessor tasksProcessor) {
        this.tasksProcessor = tasksProcessor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("starting producer bean");
        Thread thread = new Thread(() -> {
            System.out.println("Starting producer thread");
            try {
                while (true) {
                    tasksProcessor.process();
                    Thread.sleep(100);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

}
