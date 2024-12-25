package com.example.backend.models;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "notifications")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification {
    @Id
    String id;

    @NotNull
    String content;

    @NotNull
    String ownerId;

    @NotNull
    String impactPersonId;

    @NotNull
    String impactPersonProfilePic;

    @NotNull
    String impactPersonName;

    @NotNull
    String impactPersonUsername;

    String postId;
    String postPic;

    @CreatedDate
    Date createdAt;
}
