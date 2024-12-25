package com.example.backend.repository;

import com.example.backend.models.Community;
import com.example.backend.models.Post;
import com.example.backend.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommunityRepository extends MongoRepository<Community, String> {
    List<Community> getCommunitiesByMembersContainingOrderByCreatedAtDesc(User user);
}
