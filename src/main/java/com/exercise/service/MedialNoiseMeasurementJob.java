package com.exercise.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MedialNoiseMeasurementJob {

    @Autowired
    private MeasurementService measurementService;

    @Scheduled(cron = "0 * * * *") //Job to be triggered on each hour
    private void calculateMedialNoise(){
        measurementService.setMedialNoisePerSensor();
    }
}
