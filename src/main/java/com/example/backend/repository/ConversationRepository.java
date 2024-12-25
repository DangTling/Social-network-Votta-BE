package com.example.backend.repository;

import com.example.backend.models.Conversation;
import com.example.backend.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ConversationRepository extends MongoRepository<Conversation, String> {
    @Query("{ 'participants': { $all: ?0 } }")
    Conversation findByParticipantsContains(String currentUserId, String otherUserId);

    @Query("{ 'participants': { $size: 2 }, 'participants.$id': { $all: ?0 } }")
    Conversation findByParticipantIds(List<String> userIds);

    Boolean existsByParticipantsContaining(List<User> participants);

    @Query("{ $and: [ { 'participants': { $all: [?0] } }, { 'participants': { $in: ?1 } } ] }")
    List<Conversation> findConversationWithSearchValue(String currentUserId, List<String> userIds);

    List<Conversation> findByParticipantsContainsOrderByCreatedAtDesc(String currentUserId);
}
