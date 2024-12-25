package com.example.backend.response;

import com.example.backend.models.Notification;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    List<Notification> listNotification = new ArrayList<>();
    long totalNotification;
    int page;
}
