package com.assessment.fedEx;

import com.assessment.fedEx.infrastructure.Queue;
import com.assessment.fedEx.domain.API;
import com.assessment.fedEx.domain.AggregationRequest;
import com.assessment.fedEx.domain.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AggregationServiceTest {

    @Mock
    private Queue queue;

    @InjectMocks
    private AggregationService aggregationService;

    @Test
    public void whenQueryParamsAreSubmittedThenCreateEqualAmountOfRequestTasks() throws InterruptedException, ExecutionException {
        aggregationService=new AggregationService(queue);
        ArgumentCaptor<Request> argument = ArgumentCaptor.forClass(Request.class);
        aggregationService.createAggregateRequestsTask(getAggregationRequest(), null);
        verify(queue).put(argument.capture());

        assertThat(argument.getValue().getRequestTasks()).hasSize(9);
        assertThat(argument.getValue().getRequestTasks().stream().filter(task -> task.api().equals(API.PRICING))).hasSize(2);
        assertThat(argument.getValue().getRequestTasks().stream().filter(task -> task.api().equals(API.SHIPMENTS))).hasSize(3);
        assertThat(argument.getValue().getRequestTasks().stream().filter(task -> task.api().equals(API.TRACK))).hasSize(4);
    }

    private AggregationRequest getAggregationRequest() {
        return new AggregationRequest(
                List.of(2136d, 21321d, 123213d, 213d),
                List.of(213d, 21321d, 123213d),
                List.of("NL", "BE")
        );
    }
}