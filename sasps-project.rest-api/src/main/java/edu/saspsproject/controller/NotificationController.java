package edu.saspsproject.controller;

import edu.saspsproject.model.Notification;
import edu.saspsproject.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin
public class NotificationController {

    private final NotificationService notificationService;

    // Get notifications for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }
    
    // Get all notifications - ADMIN ONLY (no actual check for baseline)
    @GetMapping("/all")
    public ResponseEntity<List<Notification>> getAllNotifications(
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        
        // BASELINE: Simple hardcoded role check - no proper security
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body(null);
        }
        
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }
    
    // Get notifications by status - ADMIN ONLY
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Notification>> getNotificationsByStatus(
            @PathVariable String status,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        
        // BASELINE: Duplicated role check - no DRY principle
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body(null);
        }
        
        try {
            Notification.NotificationStatus notificationStatus = 
                Notification.NotificationStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(notificationService.getNotificationsByStatus(notificationStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
