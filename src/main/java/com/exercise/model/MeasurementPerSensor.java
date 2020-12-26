package com.exercise.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class MeasurementPerSensor {

    private Long sensorId;

    private BigDecimal value;

    private Timestamp timestamp;

    public Long getSensorId() {
        return sensorId;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
