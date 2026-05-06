package com.app.controller;

import com.app.dto.NotificationRequestDTO;
import com.app.dto.NotificationResponseDTO;
import com.app.service.NotificationService;
import com.app.utils.LoggerUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private static final String PACKAGE_NAME = NotificationController.class.getPackageName();
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<Void> createNotification(@Valid @RequestBody NotificationRequestDTO request) {
        LoggerUtil.info(PACKAGE_NAME, "Received request to create a new notification");
        notificationService.createNotification(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<NotificationResponseDTO>> getNotifications(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        LoggerUtil.info(PACKAGE_NAME, "Received request to fetch notifications for user: " + userId);
        Page<NotificationResponseDTO> notifications = notificationService.getUnreadNotificationsForUser(userId, page, size);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @RequestParam Long userId) {
        
        LoggerUtil.info(PACKAGE_NAME, "Received request to mark notification " + id + " as read");
        notificationService.markNotificationAsRead(userId, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long id,
            @RequestParam Long userId) {
        
        LoggerUtil.info(PACKAGE_NAME, "Received request to delete notification " + id);
        notificationService.deleteNotificationForUser(userId, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
