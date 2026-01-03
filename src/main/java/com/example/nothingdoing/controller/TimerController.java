package com.example.nothingdoing.controller;

import com.example.nothingdoing.dto.TimerStartRequest;
import com.example.nothingdoing.service.TimerTask;
import com.example.nothingdoing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
public class TimerController {

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, TimerTask> activeTimers = new ConcurrentHashMap<>();

    private final UserService userService;
    @MessageMapping("/timer/start")
    public void startTimer(@Header("simpSessionId") String sessionId,
                           Principal principal,
                           TimerStartRequest request) {

        cancelTimer(sessionId);

        String username = principal.getName();

        TimerTask task = new TimerTask(
                username,
                request.getDuration(),
                request.getInterval(),
                messagingTemplate
        );

        // 🔥 ВАЖНО: начисляем минуты ПО ЗАВЕРШЕНИЮ
        task.setOnFinish(() -> {
            long minutes = request.getDuration() / 60;
            userService.addMinutes(username, minutes);
            System.out.println("Добавлено " + minutes + " минут пользователю " + username);
        });

        activeTimers.put(sessionId, task);
        task.start();

        messagingTemplate.convertAndSendToUser(username, "/queue/sound", "start");
    }



    @MessageMapping("/timer/cancel")
    public void cancelTimer(@Header("simpSessionId") String sessionId) {
        TimerTask task = activeTimers.remove(sessionId);
        if (task != null) task.cancel();
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        cancelTimer(event.getSessionId());
    }
}