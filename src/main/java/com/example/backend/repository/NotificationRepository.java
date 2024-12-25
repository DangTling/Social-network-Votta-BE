package com.example.backend.repository;

import com.example.backend.models.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findNotificationsByOwnerIdOrderByCreatedAtDesc(String ownerId, Pageable pageable);

    Long countNotificationByOwnerId(String ownerId);
}
