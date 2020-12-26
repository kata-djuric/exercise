package com.exercise.rest;

import com.exercise.model.MeasurementPerSensor;
import com.exercise.model.MedianNoisePerSensorResponse;
import com.exercise.service.MeasurementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping(value = "measurement-record")
public class MeasurementResource {

    @Autowired
    private MeasurementService measurementService;

    @PostMapping
    ResponseEntity addMeasurementPerSensor(@RequestBody MeasurementPerSensor measurementPerSensor) {
        measurementService.addMeasurementPerSensor(measurementPerSensor);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    ResponseEntity<List<MedianNoisePerSensorResponse>> getMedianNoisePerSensor(
            @RequestParam(name = "sensorId") Long sensorId,
            @RequestParam(name = "startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(name = "endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    ) {
        return ResponseEntity.ok()
                .body(measurementService.getMedialNoiseMeasurementInTimeRange(sensorId, startTime, endTime));
    }

}

