package com.example.backend.models;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "communities")
public class Community {
    @Id
    String id;

    @NotNull
    String name;

    @NotNull
    String description;

    @NotNull
    String profilePic;

    @DBRef
    List<User> members = new ArrayList<>();

    @DBRef
    User founder;

    @CreatedDate
    Date createdAt;

    @DBRef
    List<User> pendingRequests = new ArrayList<>();
}
