package com.example.backend.models;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "conversations")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Conversation {
    @Id
    String id;

    @DBRef
    List<User> participants = new ArrayList<>();

    LastMessage lastMessage;

    @CreatedDate
    Date createdAt;

    @LastModifiedDate
    Date updatedAt;
}
