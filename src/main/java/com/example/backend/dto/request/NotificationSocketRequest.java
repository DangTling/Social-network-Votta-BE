package com.example.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationSocketRequest {
    @NotNull
    String receiverId;

    @NotNull
    String senderId;

    @NotNull
    String message;
}
