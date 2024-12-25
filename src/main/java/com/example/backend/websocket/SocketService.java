package com.example.backend.websocket;

import com.corundumstudio.socketio.SocketIOClient;
import com.example.backend.models.Message;
import com.example.backend.services.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocketService {
    private final ConversationService conversationService;

    public void sendSocketmessage(SocketIOClient senderClient, Message message, String room) {
        for (
                SocketIOClient client: senderClient.getNamespace().getRoomOperations(room).getClients()
        ) {
            if (!client.getSessionId().equals(senderClient.getSessionId())) {
                client.sendEvent("read_message", message);
            }
        }
    }
}
