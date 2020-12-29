package com.exercise.service;

import com.exercise.generated.renovero.tables.records.MeasurementRecord;
import com.exercise.model.MeasurementPerSensor;
import com.exercise.model.MedianNoisePerSensorResponse;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.exercise.generated.renovero.tables.Measurement.MEASUREMENT;
import static com.exercise.generated.renovero.tables.MedianNoiseLevelPerSensor.MEDIAN_NOISE_LEVEL_PER_SENSOR;
import static org.jooq.impl.DSL.avg;

@Service
public class MeasurementService {

    @Autowired
    private DSLContext create;

    /**
     * Writing into table MEASUREMENT data received from sensor
     *
     * @param measurementPerSensor - data received from sensor
     */

    @Transactional
    public void addMeasurementPerSensor(MeasurementPerSensor measurementPerSensor) {
        MeasurementRecord measurementRecord = create.newRecord(MEASUREMENT);
        measurementRecord.setSensorId(measurementPerSensor.getSensorId());
        measurementRecord.setValue(measurementPerSensor.getValue());
        measurementRecord.setTimestamp(measurementPerSensor.getTimestamp());
        measurementRecord.store();
    }

    /**
     * For the given sensor id and time range, extracts from the table MEDIAN_NOISE_LEVEL_PER_SENSOR
     * medial noise for that sensor and the timestamp when it is calculated.
     *
     * @param sensorId  - sensor id
     * @param startTime - start time for extracting medial noise per sensor
     * @param endTime   - end time for extracting medial noise per sensor
     * @return list of values and timestamps
     */

    @Transactional
    public List<MedianNoisePerSensorResponse> getMedialNoiseMeasurementInTimeRange(
            Long sensorId,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        return create.select(MEDIAN_NOISE_LEVEL_PER_SENSOR.VALUE, MEDIAN_NOISE_LEVEL_PER_SENSOR.TIMESTAMP)
                .from(MEDIAN_NOISE_LEVEL_PER_SENSOR)
                .where(MEDIAN_NOISE_LEVEL_PER_SENSOR.SENSOR_ID.equal(sensorId))
                .and(MEDIAN_NOISE_LEVEL_PER_SENSOR.TIMESTAMP
                        .between(Timestamp.valueOf(startTime), Timestamp.valueOf(endTime)))
                .fetch().into(MedianNoisePerSensorResponse.class);
    }

    /**
     * Calculates medial noise per sensor for the previous hour and writes it into table MEDIAN_NOISE_LEVEL_PER_SENSOR.
     */

    public void setMedialNoisePerSensor() {
        LocalDateTime currentLocalDateTime = LocalDateTime.now();
        Timestamp currentTimestamp = Timestamp.valueOf(currentLocalDateTime);
        Timestamp currentTimestampSubHour = Timestamp.valueOf(currentLocalDateTime.minusHours(1));

        Map<Long, BigDecimal> record = getMedialNoiseMeasurementPerSensor(currentTimestampSubHour, currentTimestamp);
        record.keySet().forEach(key -> create.insertInto(MEDIAN_NOISE_LEVEL_PER_SENSOR,
                MEDIAN_NOISE_LEVEL_PER_SENSOR.SENSOR_ID,
                MEDIAN_NOISE_LEVEL_PER_SENSOR.VALUE,
                MEDIAN_NOISE_LEVEL_PER_SENSOR.TIMESTAMP)
                .values(key, record.get(key), Timestamp.valueOf(currentTimestamp.toString()))
                .execute());
    }

    /**
     * From existing table MEASUREMENT, for each sensor medial noise is calculated for the given
     * time range.
     *
     * @param currentTimestampSubHour - start of the time range
     * @param currentTimestamp        - end of the time range
     * @return map where the key is sensor id and value medial value
     */

    private Map<Long, BigDecimal> getMedialNoiseMeasurementPerSensor(Timestamp currentTimestampSubHour,
                                                                     Timestamp currentTimestamp) {
        return create.select(MEASUREMENT.SENSOR_ID, avg(MEASUREMENT.VALUE))
                .from(MEASUREMENT)
                .where(MEASUREMENT.TIMESTAMP.between(currentTimestampSubHour, currentTimestamp))
                .groupBy(MEASUREMENT.SENSOR_ID)
                .fetchMap(Record2::value1, Record2::value2);
    }

}
