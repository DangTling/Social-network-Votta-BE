package com.example.backend.models;

import jakarta.validation.constraints.Null;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "messages")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message {
    @Id
    String id;

    @Null
    String text;

    @Null
    String fileName;

    @Null
    String fileType;

    @DBRef
    User sender;

    @DBRef
    Conversation conversation;

    @CreatedDate
    Date createdAt;

    @LastModifiedDate
    Date updatedAt;

    @Null
    String replyId;
}
