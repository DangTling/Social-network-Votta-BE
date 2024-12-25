package com.example.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationRequest {
    @NotNull
    String ownerId;

    @NotNull
    String content;

    @NotNull
    String affectedUserId;

    @NotNull
    String affectedUserPic;

    @NotNull
    String affectedName;

    @NotNull
    String affectedUsername;

    String postId;
    String postPic;
}
