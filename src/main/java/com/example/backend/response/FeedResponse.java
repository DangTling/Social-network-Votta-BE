package com.example.backend.response;

import com.example.backend.models.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedResponse {
    List<Post> feedPosts = new ArrayList<>();
    long totalSize;
}
