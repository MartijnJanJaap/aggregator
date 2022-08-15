package com.assessment.fedEx;

import com.assessment.fedEx.domain.API;
import com.assessment.fedEx.domain.Request;
import com.assessment.fedEx.domain.RequestTask;
import com.assessment.fedEx.infrastructure.Queue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TasksProcessorTest {

    @Mock
    private Queue queue;

    @Mock
    private XYZClient XYZClient;

    @InjectMocks
    private TasksProcessor tasksProcessor;

    @Test
    public void whenNotEnoughParamsThenProcessTasksAfterFiveSeconds() throws Exception {
        List<Request> requests = List.of(request( "FR", "AK", "AF"));

        when(queue.size()).thenReturn(requests.size());
        when(queue.getAll()).thenReturn(requests);

        tasksProcessor.process();
        Thread.sleep(1000);
        verify(XYZClient, times(0)).getPricing(any());
        Thread.sleep(4000);
        tasksProcessor.process();
        verify(XYZClient, times(1)).getPricing(any());
    }

    @Test
    public void whenEnoughParamsThenProcessImmediately() throws Exception {
        List<Request> requests = List.of(request("NL", "BE", "FR", "AK", "AF"));

        when(queue.size()).thenReturn(requests.size());
        when(queue.getAll()).thenReturn(requests);

        tasksProcessor.process();

        verify(XYZClient, times(1)).getPricing(any());
    }

    private Request request(String... pricingParams){
        return new Request(
                Mockito.mock(DeferredResult.class),
                pricingTasks(pricingParams)
        );
    }

    private Set<RequestTask> pricingTasks(String... params) {
        return Arrays.stream(params).map(param -> requestTask(API.PRICING, param)).collect(Collectors.toSet());
    }

    private RequestTask requestTask(API api, String param) {
        return new RequestTask(
                api,
                param,
                LocalDateTime.now()
        );
    }


}