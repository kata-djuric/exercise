package com.exercise.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class MedianNoisePerSensorResponse {

    @JsonProperty(value = "value")
    private BigDecimal value;

    @JsonProperty(value = "timestamp")
    private Timestamp timestamp;

    private MedianNoisePerSensorResponse() {
    }

    private MedianNoisePerSensorResponse(BigDecimal value, Timestamp timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }
}
