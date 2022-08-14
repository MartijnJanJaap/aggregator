package com.assessment.fedEx;

import com.assessment.fedEx.domain.Request;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

@SpringBootApplication
public class FedExApplication {

	public static void main(String[] args) {
		SpringApplication.run(FedExApplication.class, args);
	}

}
