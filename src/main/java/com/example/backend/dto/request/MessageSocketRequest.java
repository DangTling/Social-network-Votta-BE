package com.example.backend.dto.request;

import com.example.backend.models.Message;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageSocketRequest {
    @NotNull
    Message message;

    @NotNull
    String receiverId;
}
