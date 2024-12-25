package com.example.backend.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Reply {
    @DBRef
    User userId;

    @NotNull
    @Size(min = 1, max = 400, message = "Exceed the number of permitted characters")
    String text;

    @NotNull
    String userProfilePic;

    @NotNull
    String username;

    Date createdAt;
}
