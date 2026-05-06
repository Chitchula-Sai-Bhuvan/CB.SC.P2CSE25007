package com.app.service;

import com.app.dto.NotificationRequestDTO;
import com.app.dto.NotificationResponseDTO;
import com.app.exception.ResourceNotFoundException;
import com.app.model.Notification;
import com.app.model.User;
import com.app.model.UserNotification;
import com.app.repository.NotificationRepository;
import com.app.repository.UserNotificationRepository;
import com.app.repository.UserRepository;
import com.app.utils.LoggerUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private static final String PACKAGE_NAME = NotificationService.class.getPackageName();

    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserNotificationRepository userNotificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userNotificationRepository = userNotificationRepository;
        this.userRepository = userRepository;
    }

    @Async
    @Transactional
    public void createNotification(NotificationRequestDTO request) {
        LoggerUtil.info(PACKAGE_NAME, "Creating new notification of type: " + request.getType());

        Notification notification = new Notification(request.getMessage(), request.getType());
        notification = notificationRepository.save(notification);

        List<UserNotification> userNotifications = new ArrayList<>();
        for (Long userId : request.getUserIds()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

            UserNotification userNotification = new UserNotification(user.getId(), notification.getId());
            userNotifications.add(userNotification);
        }

        userNotificationRepository.saveAll(userNotifications);
        LoggerUtil.info(PACKAGE_NAME, "Notification successfully created and assigned to " + request.getUserIds().size() + " users.");
    }

    public Page<NotificationResponseDTO> getUnreadNotificationsForUser(Long userId, int page, int size) {
        LoggerUtil.info(PACKAGE_NAME, "Fetching unread notifications for user ID: " + userId);
        
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<UserNotification> unreadUserNotifications = userNotificationRepository
                .findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false, pageable);

        return unreadUserNotifications.map(userNotification -> {
            Notification notification = notificationRepository.findById(userNotification.getNotificationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
            
            return new NotificationResponseDTO(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getType(),
                    userNotification.getIsRead(),
                    userNotification.getCreatedAt()
            );
        });
    }

    @Transactional
    public void markNotificationAsRead(Long userId, Long notificationId) {
        LoggerUtil.info(PACKAGE_NAME, "Marking notification " + notificationId + " as read for user " + userId);
        
        UserNotification userNotification = userNotificationRepository.findByUserIdAndNotificationId(userId, notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification link not found for user ID " + userId + " and notification ID " + notificationId));

        userNotification.setIsRead(true);
        userNotificationRepository.save(userNotification);
    }

    @Transactional
    public void deleteNotificationForUser(Long userId, Long notificationId) {
        LoggerUtil.info(PACKAGE_NAME, "Deleting notification " + notificationId + " for user " + userId);
        
        UserNotification userNotification = userNotificationRepository.findByUserIdAndNotificationId(userId, notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification link not found for user ID " + userId + " and notification ID " + notificationId));

        userNotificationRepository.delete(userNotification);
    }
}
