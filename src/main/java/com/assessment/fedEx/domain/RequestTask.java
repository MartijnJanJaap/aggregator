package com.assessment.fedEx.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public record RequestTask(API api, String requestParam, LocalDateTime dateTime) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestTask that = (RequestTask) o;
        return api == that.api && requestParam.equals(that.requestParam);
    }

    @Override
    public int hashCode() {
        return Objects.hash(api, requestParam);
    }
}
