package com.example.backend.repository;

import com.example.backend.models.Conversation;
import com.example.backend.models.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findAllByConversationOrderByCreatedAt(Conversation conversation, Pageable pageable);

    List<Message> findAllByConversationOrderByCreatedAt(Conversation conversation);

    long countByConversation(Conversation conversation);
    Message deleteMessageById(String id);
}
