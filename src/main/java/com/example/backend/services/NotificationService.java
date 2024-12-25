package com.example.backend.services;

import com.example.backend.dto.request.NotificationRequest;
import com.example.backend.models.Notification;
import com.example.backend.repository.NotificationRepository;
import com.example.backend.response.NotificationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public ResponseEntity save(@RequestBody NotificationRequest notificationRequest) {
        Notification notification = new Notification();
        notification.setContent(notificationRequest.getContent());
        notification.setOwnerId(notificationRequest.getOwnerId());
        notification.setPostId(notificationRequest.getPostId());
        notification.setPostPic(notificationRequest.getPostPic());
        notification.setImpactPersonId(notificationRequest.getAffectedUserId());
        notification.setImpactPersonProfilePic(notificationRequest.getAffectedUserPic());
        notification.setImpactPersonName(notificationRequest.getAffectedName());
        notification.setImpactPersonUsername(notificationRequest.getAffectedUsername());
        Notification notify = notificationRepository.save(notification);
        if (notify.getClass()==Notification.class) {
            return ResponseEntity.status(HttpStatus.OK).body(notify);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error saving notification");
    }

    public ResponseEntity get(@RequestParam String id) {
        Notification notify = notificationRepository.findById(id).orElse(null);
        if (notify == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(notify);
    }

    public ResponseEntity delete(@RequestParam String id) {
        Notification notify = notificationRepository.findById(id).orElse(null);
        if (notify == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification not found");
        }
        notificationRepository.delete(notify);
        return ResponseEntity.status(HttpStatus.OK).body("Notification deleted");
    }

    public ResponseEntity getAll(@RequestParam String ownerId, @RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Notification> list = notificationRepository.findNotificationsByOwnerIdOrderByCreatedAtDesc(ownerId, pageable);
        Long totalNotification = notificationRepository.countNotificationByOwnerId(ownerId);

        return ResponseEntity.status(HttpStatus.OK).body(new NotificationResponse(list, totalNotification, page));
    }

}
