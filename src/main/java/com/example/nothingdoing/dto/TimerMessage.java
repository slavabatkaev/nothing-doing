package com.example.nothingdoing.dto;

import lombok.Data;

@Data
public class TimerMessage {
    private long remaining;
    private long total;
    private boolean finished;

    public TimerMessage(long remaining, long total, boolean finished) {
        this.remaining = remaining;
        this.total = total;
        this.finished = finished;
    }
}