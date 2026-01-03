package com.example.nothingdoing.dto;

import lombok.Data;

@Data
public class TimerStartRequest {
    private long duration;
    private long interval;
}