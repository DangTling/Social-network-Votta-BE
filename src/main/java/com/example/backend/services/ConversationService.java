package com.example.backend.services;

import com.example.backend.dto.request.MessageRequest;
import com.example.backend.models.Conversation;
import com.example.backend.models.LastMessage;
import com.example.backend.models.Message;
import com.example.backend.models.User;
import com.example.backend.repository.ConversationRepository;
import com.example.backend.repository.MessageRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConversationService {
    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    public Message sendMessage(String currentUserId, MessageRequest message) {
        if (currentUserId.equals(message.getReceiverId())) {
            throw new IllegalArgumentException("You cannot send a message to yourself");
        }
        User currentUser = userRepository.findById(currentUserId).orElseThrow(()->new IllegalArgumentException("Please login first"));
        User receiver = userRepository.findById(message.getReceiverId()).orElseThrow(()->new IllegalArgumentException("User not found"));
        List<User> participants = Arrays.asList(currentUser, receiver);
        List<String> userIds = Arrays.asList(currentUserId, receiver.getId());

        LastMessage lastMessage = new LastMessage();
        lastMessage.setSender(currentUser);
        lastMessage.setText(message.getMessage());

        List<Conversation> allConversations = conversationRepository.findAll();
        for (Conversation conversation : allConversations) {
            List<String> participantIds = conversation.getParticipants().stream().map(User::getId).collect(Collectors.toList());
            if (participantIds.containsAll(userIds) && participantIds.size() == 2) {
                conversation.setLastMessage(lastMessage);
                conversationRepository.save(conversation);
                Message newMessage = new Message();
                newMessage.setSender(currentUser);
                newMessage.setConversation(conversation);
                newMessage.setText(message.getMessage());

                if (message.getFileName()!=null && message.getFileType()!=null) {
                    newMessage.setFileName(message.getFileName());
                    newMessage.setFileType(message.getFileType());
                }

                if (message.getReplyId() != null) {
                    Message replyMessage = messageRepository.findById(message.getReplyId()).orElseThrow(() -> new IllegalArgumentException("Message not found"));

                    newMessage.setReplyId(replyMessage.getId());
                }

                return messageRepository.save(newMessage);
            }
        }
        Conversation conversation= new Conversation();
        conversation.setLastMessage(lastMessage);
        conversation.setParticipants(participants);
        conversationRepository.save(conversation);
        Message newMessage = new Message();
        newMessage.setSender(currentUser);
        newMessage.setConversation(conversation);
        newMessage.setText(message.getMessage());
        if (message.getFileName()!=null && message.getFileType()!=null) {
            newMessage.setFileName(message.getFileName());
            newMessage.setFileType(message.getFileType());
        }
        return messageRepository.save(newMessage);
    }

    public MessageResponse getMessages(String currentUserId, String otherUserId, int page, int pageSize) {
        if (!userRepository.existsById(currentUserId)) {
            throw new IllegalArgumentException("Please login first");
        }
        if (!userRepository.existsById(otherUserId)) {
            throw new IllegalArgumentException("User not found");
        }
        User currentUser = userRepository.findById(currentUserId).orElseThrow();
        User sender = userRepository.findById(otherUserId).orElseThrow();
        List<String> userIds = Arrays.asList(currentUserId, otherUserId);

        List<Conversation> allConversations = conversationRepository.findAll();
        for (Conversation conversation:allConversations) {
            List<String> participantIds = conversation.getParticipants().stream().map(User::getId).collect(Collectors.toList());
            if (participantIds.containsAll(userIds) && participantIds.size() == 2) {
                Pageable pageable = PageRequest.of(page, pageSize);
                return new MessageResponse(messageRepository.findAllByConversationOrderByCreatedAt(conversation, pageable), messageRepository.countByConversation(conversation));
            }
        }

        return new MessageResponse(new ArrayList<Message>(), 0);
    }

    public void removeMessage(String messageId, String currentUserId) {
        User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Message message = messageRepository.findById(messageId).orElseThrow(()-> new IllegalArgumentException("Message not found"));
        Conversation conversation = message.getConversation();

        if (message.getSender().getId().equals(currentUserId)) {
            messageRepository.deleteMessageById(messageId);
            List<Message> messages = messageRepository.findAllByConversationOrderByCreatedAt(conversation);

            if (messages.size() > 0) {
                LastMessage lastMessage = new LastMessage(messages.getLast().getText(), messages.getLast().getSender());

                conversation.setLastMessage(lastMessage);
                conversationRepository.save(conversation);
            }
        } else {
            throw new IllegalArgumentException("You cannot delete this message");
        }
    }

    public Message getMessageById(String messageId, String currentUserId) {
        User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new IllegalArgumentException("You should login first"));
        return messageRepository.findById(messageId).orElse(Message.builder().text("Message not found").build());
    }

    public List getConversation(String currentUserId, String queryName) {
        List<User> listUserFound = userRepository.findByUsernameRegex(queryName);
        List<String> userIds = listUserFound.stream().map(User::getId).toList();

        if (queryName!=null && !queryName.trim().isEmpty()) {
//            return conversationRepository.findConversationWithSearchValue(currentUserId, userIds);
            User currentUser = userRepository.findById(currentUserId).get();
            List<String> following = currentUser.getFollowing();
            List<String> follower = currentUser.getFollower();
            Set<String> combinedSet = new HashSet<>(following);
            combinedSet.addAll(follower);
            List<User> listFound = combinedSet.stream()
                    .map(userId -> userRepository.findById(userId))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(user -> user.getUsername().toLowerCase().contains(queryName.toLowerCase()) || user.getName().toLowerCase().contains(queryName.toLowerCase()))
                    .collect(Collectors.toList());

            return listFound;
        }

        return conversationRepository.findByParticipantsContainsOrderByCreatedAtDesc(currentUserId);
    }

}
