package com.willbank.notification.controller;

import com.willbank.notification.entity.Notification;
import com.willbank.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @GetMapping("/destinataire/{destinataire}")
    public ResponseEntity<Page<Notification>> getNotificationsByDestinataire(
            @PathVariable String destinataire,
            Pageable pageable) {
        log.info("GET /api/notifications/destinataire/{}", destinataire);
        Page<Notification> notifications = notificationService.getNotificationsByDestinataire(destinataire, pageable);
        return ResponseEntity.ok(notifications);
    }
    
    @PostMapping("/retry")
    public ResponseEntity<Map<String, String>> retryFailedNotifications() {
        log.info("POST /api/notifications/retry");
        notificationService.retryFailedNotifications();
        return ResponseEntity.ok(Map.of("message", "Retry process started"));
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "notification-service"));
    }
}
