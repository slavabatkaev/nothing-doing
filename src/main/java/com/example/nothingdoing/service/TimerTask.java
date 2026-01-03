package com.example.nothingdoing.service;

import com.example.nothingdoing.dto.TimerMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.concurrent.*;

@RequiredArgsConstructor
public class TimerTask {
    private final String username;
    private final long totalSeconds;
    private final long intervalSeconds;
    private final SimpMessagingTemplate template;

    private Runnable onFinish; //от ЧТГПТ
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> future;
    private long elapsed = 0;

    public void start() {
        future = executor.scheduleAtFixedRate(() -> {
            elapsed++;
            long remaining = totalSeconds - elapsed;

            if (intervalSeconds > 0 && elapsed % intervalSeconds == 0 && remaining > 0) {
                template.convertAndSendToUser(username, "/queue/sound", "tick");
            }

            template.convertAndSendToUser(username, "/queue/timer",
                    new TimerMessage(remaining, totalSeconds, remaining <= 0));

            if (remaining <= 0) {
                template.convertAndSendToUser(username, "/queue/sound", "end");
                if (onFinish != null) {
                    onFinish.run();
                }

                cancel();
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public void cancel() {
        if (future != null) future.cancel(true);
        executor.shutdown();
    }

    public void setOnFinish(Runnable onFinish) {
        this.onFinish = onFinish;
    }
}