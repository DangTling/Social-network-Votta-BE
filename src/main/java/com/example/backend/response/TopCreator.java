package com.example.backend.response;

import com.example.backend.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopCreator {
    private User user;
    private int postCount;
    private int totalLikes;
}
