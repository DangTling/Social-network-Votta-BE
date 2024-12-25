package com.example.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeetingSocketRequest {
    String meetingId;

    @NotNull
    String receiverId;

    @NotNull
    String senderId;
}
