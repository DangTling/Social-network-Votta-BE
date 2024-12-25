package com.example.backend.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "posts")
public class Post {
    @Id
    String id;

    @DBRef
    User postedBy;

    @NotNull
    @Size(min = 1, max = 10000, message = "Caption must be at least 1 character")
    String caption;

    @NotNull
    String postPic;

    @DBRef
    List<User> like = new ArrayList<>();

    List<Reply> replies = new ArrayList<>();

    @NotNull
    String tags;

    @DBRef
    List<User> savedIn = new ArrayList<>();

    @CreatedDate
    Date createdAt;

    @Null
    Community belongsTo;

    Boolean pending;
}

