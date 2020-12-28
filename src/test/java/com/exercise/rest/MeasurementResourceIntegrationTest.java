package com.exercise.rest;

import com.exercise.BaseIntegrationTest;
import com.exercise.service.MeasurementService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class MeasurementResourceIntegrationTest extends BaseIntegrationTest {

    private static final Long SENSOR_ID_1 = 1L;
    private static final Long SENSOR_ID_2 = 2L;

    private static final BigDecimal SENSOR_VALUE_1 = BigDecimal.valueOf(30);
    private static final BigDecimal SENSOR_VALUE_2 = BigDecimal.valueOf(70);

    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final LocalDateTime SENSOR_INPUT_TIME_1 = CURRENT_TIME.minusHours(1).minusMinutes(30);
    private static final LocalDateTime SENSOR_INPUT_TIME_2 = CURRENT_TIME.minusHours(1).minusMinutes(31);

    private static final LocalDateTime MEASUREMENT_START_TIME = CURRENT_TIME.minusHours(2);
    private static final LocalDateTime MEASUREMENT_END_TIME = CURRENT_TIME.plusHours(2);

    private static final String SENSOR_ID_PARAM = "sensorId";
    private static final String START_TIME_PARAM = "startTime";
    private static final String END_TIME_PARAM = "endTime";

    private static final String MEASUREMENT_PATH = "/measurement-record";

    @Autowired
    private MeasurementService measurementService;

    @Test
    public void should_add_measurement_per_sensor_calculate_medial_noise_and_get_it_for_one_sensor() {

        // post measurement per sensor
        postMeasurementPerSensor(SENSOR_ID_1, SENSOR_VALUE_1, SENSOR_INPUT_TIME_1);

        //job triggering
        measurementService.setMedialNoisePerSensor();

        // get measurement per sensor
        String result =getMedialNoisePerSensor(SENSOR_ID_1);
        assertTrue(result.contains("\"value\":3E+1"));
    }

    @Test
    public void should_add_measurement_per_sensor_calculate_medial_noise_and_get_it_for_one_sensor_with_multiple_records() {

        // post measurements per sensor
        postMeasurementPerSensor(SENSOR_ID_2, SENSOR_VALUE_1, SENSOR_INPUT_TIME_1);
        postMeasurementPerSensor(SENSOR_ID_2, SENSOR_VALUE_2, SENSOR_INPUT_TIME_2);

        //job triggering
        measurementService.setMedialNoisePerSensor();

        // get measurement per sensor
        String result = getMedialNoisePerSensor(SENSOR_ID_2);
        assertTrue(result.contains("\"value\":5E+1"));
    }

    private String getMedialNoisePerSensor(Long sensorId) {
        return given()
                .queryParam(SENSOR_ID_PARAM, sensorId)
                .queryParam(START_TIME_PARAM, MEASUREMENT_START_TIME.toString())
                .queryParam(END_TIME_PARAM, MEASUREMENT_END_TIME.toString())
                .when().
                        get(MEASUREMENT_PATH).
                        then().
                        statusCode(OK.value()).
                        extract()
                .asString();
    }

    private void postMeasurementPerSensor(
            Long sensorId,
            BigDecimal sensorValue,
            LocalDateTime sensorInputTime
    ){
        given().
                contentType(JSON).
                body(getBodyForMeasurementPerSensor(sensorId, sensorValue, sensorInputTime)).
                when().
                post(MEASUREMENT_PATH).
                then().
                statusCode(OK.value());
    }

    private String getBodyForMeasurementPerSensor(
            Long sensorId,
            BigDecimal sensorValue,
            LocalDateTime sensorInputTime) {
        return "{\n" +
                "  \"sensorId\": " + sensorId + ",\n" +
                "  \"value\": " + sensorValue + ",\n" +
                "  \"timestamp\": \"" + sensorInputTime + "\"\n" +
                "}";
    }

}
