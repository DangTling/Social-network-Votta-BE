package com.example.backend.websocket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.example.backend.dto.request.*;
import com.example.backend.models.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SocketModule {
    private final SocketIOServer server;
    private final SocketService socketService;
    private final Map<String, String> userSocketMap = new HashMap<>();


    public SocketModule(SocketIOServer server, SocketService socketService) {
        this.server = server;
        this.socketService = socketService;
        server.addConnectListener(this.onConnected());
        server.addDisconnectListener(this.onDisconnected());
        server.addEventListener("sendMessage", MessageSocketRequest.class, this.onChatReceived());
        server.addEventListener("removeMessage", MessageSocketRequest.class, this.onChatDelete());
        server.addEventListener("sendFile", MessageSocketRequest.class, this.onFileSend());
        server.addEventListener("sendMeetingId", MeetingSocketRequest.class, this.onMeetingSend());
        server.addEventListener("cancelCall", MeetingSocketRequest.class, this.onMeetingCancel());
        server.addEventListener("leaveMeeting", MeetingSocketRequest.class, this.onMeetingLeave());
        server.addEventListener("User is in a meeting", MeetingSocketRequest.class, this.onMeetingBusy());
        server.addEventListener("sendNotify", NotificationSocketRequest.class, this.onNotifySend());
        server.addEventListener("sendComment", CommentSocketRequest.class, this.onCommentSend());
    }

    private ConnectListener onConnected() {
        return (client) -> {
            String userID = client.getHandshakeData().getSingleUrlParam("userId");
            if (userID != null && !userID.isEmpty()) {
                userSocketMap.put(userID, client.getSessionId().toString());
                System.out.println(client.getHandshakeData().getSingleUrlParam("userId") + " connected");
            }
        };

    }

    private DisconnectListener onDisconnected() {
        return (client) -> {
            String userID = client.getHandshakeData().getSingleUrlParam("userId");
            if (userID != null) {
                userSocketMap.remove(userID);
                System.out.println(client.getHandshakeData().getSingleUrlParam("userId") + " disconnected");
            }
        };
    }

    private DataListener<MessageSocketRequest> onChatReceived() {
        return (senderClient, data, ackSender) -> {
            String recipientID = data.getReceiverId();
            String recipientSocketID = userSocketMap.get(recipientID);

            if (recipientSocketID != null) {
                SocketIOClient recipientClient = server.getClient(UUID.fromString(recipientSocketID));

                if (recipientClient != null && recipientClient.isChannelOpen()) {

                    recipientClient.sendEvent("newMessage", data.getMessage());
                    System.out.println("Message sent to " + recipientID);
                } else {
                    System.out.println(recipientID + " is not online");
                }
            } else {
                System.out.println("Recipient socket not found");
            }
        };
    }

    private DataListener<MessageSocketRequest> onChatDelete() {
        return (senderClient, data, ackSender) -> {
            String recipientID = data.getReceiverId();
            String recipientSocketID = userSocketMap.get(recipientID);
            if (recipientSocketID != null) {
                SocketIOClient recipientClient = server.getClient(UUID.fromString(recipientSocketID));
                if (recipientClient != null && recipientClient.isChannelOpen()) {
                    recipientClient.sendEvent("deleteMessage", data.getMessage());
                    System.out.println("Message is deleted ");
                } else {
                    System.out.println(recipientID + " is not online");
                }
            } else {
                System.out.println("Recipient socket not found");
            }
        };
    }

    private DataListener<MessageSocketRequest> onFileSend() {
        return (senderClient, data, ackSender) -> {
            String recipientID = data.getReceiverId();
            String recipientSocketID = userSocketMap.get(recipientID);
            if (recipientSocketID != null) {
                SocketIOClient recipientClient = server.getClient(UUID.fromString(recipientSocketID));
                if (recipientClient != null && recipientClient.isChannelOpen()) {
                    recipientClient.sendEvent("newFile", data.getMessage());
                    System.out.println("File sent to " + recipientID);
                } else {
                    System.out.println(recipientID + " is not online");
                }
            } else {
                System.out.println("Recipient socket not found");
            }
        };
    }

    private DataListener<MeetingSocketRequest> onMeetingSend() {
        return (senderClient, data, ackSender) -> {
            String recipientID = data.getReceiverId();
            String recipientSocketID = userSocketMap.get(recipientID);
            if (recipientSocketID != null) {
                SocketIOClient recipientClient = server.getClient(UUID.fromString(recipientSocketID));
                if (recipientClient != null && recipientClient.isChannelOpen()) {
                    recipientClient.sendEvent("newMeeting", data);
                    System.out.println("File sent to " + recipientID);
                } else {
                    System.out.println(recipientID + " is not online");
                }
            } else {
                System.out.println(recipientID + " is not online");
                sendNotificationBackToSender(senderClient, data);
            }
        };
    }

    private void sendNotificationBackToSender(SocketIOClient senderClient, MeetingSocketRequest data) {
        String recipientID = data.getSenderId();
        String recipientSocketID = userSocketMap.get(recipientID);
        if (recipientSocketID != null) {
            SocketIOClient recipientClient = server.getClient(UUID.fromString(recipientSocketID));
            if (recipientClient != null && recipientClient.isChannelOpen()) {
                recipientClient.sendEvent("notOnline", data);
                System.out.println("File sent to " + recipientID);
            } else {
                System.out.println(recipientID + " is not online");
            }
        }
    }

    private DataListener<MeetingSocketRequest> onMeetingCancel() {
        return (senderClient, data, ackSender) -> {
            String recipientID = data.getReceiverId();
            String recipientSocketID = userSocketMap.get(recipientID);
            if (recipientSocketID != null) {
                SocketIOClient recipientClient = server.getClient(UUID.fromString(recipientSocketID));
                if (recipientClient != null && recipientClient.isChannelOpen()) {
                    recipientClient.sendEvent("canceledMeeting", data);
                    System.out.println("Signal sent to " + recipientID);
                } else {
                    System.out.println(recipientID + " is not online");
                }
            } else {
                System.out.println("Recipient socket not found");
            }
        };
    }

    private DataListener<MeetingSocketRequest> onMeetingBusy() {
        return (senderClient, data, ackSender) -> {
            String recipientID = data.getReceiverId();
            String recipientSocketID = userSocketMap.get(recipientID);
            if (recipientSocketID != null) {
                SocketIOClient recipientClient = server.getClient(UUID.fromString(recipientSocketID));
                if (recipientClient != null && recipientClient.isChannelOpen()) {
                    recipientClient.sendEvent("busyMeeting", data);
                    System.out.println("Signal sent to " + recipientID);
                } else {
                    System.out.println(recipientID + " is not online");
                }
            } else {
                System.out.println("Recipient socket not found");
            }
        };
    }

    private DataListener<MeetingSocketRequest> onMeetingLeave() {
        return (senderClient, data, ackSender) -> {
            String recipientID = data.getReceiverId();
            String recipientSocketID = userSocketMap.get(recipientID);
            if (recipientSocketID != null) {
                SocketIOClient recipientClient = server.getClient(UUID.fromString(recipientSocketID));
                if (recipientClient != null && recipientClient.isChannelOpen()) {
                    recipientClient.sendEvent("leavedMeeting", data);
                    System.out.println("Signal sent to " + recipientID);
                } else {
                    System.out.println(recipientID + " is not online");
                }
            } else {
                System.out.println("Recipient socket not found");
            }
        };
    }

    private DataListener<NotificationSocketRequest> onNotifySend() {
        return (senderClient, data, ackSender) -> {
            String recipientID = data.getReceiverId();
            String recipientSocketID = userSocketMap.get(recipientID);
            if (recipientSocketID != null) {
                SocketIOClient recipientClient = server.getClient(UUID.fromString(recipientSocketID));
                if (recipientClient != null && recipientClient.isChannelOpen()) {
                    recipientClient.sendEvent("newNotify", data);
                    System.out.println("Signal sent to " + recipientID);
                } else {
                    System.out.println(recipientID + " is not online");
                }
            } else {
                System.out.println("Recipient socket not found");
            }
        };
    }

    private DataListener<CommentSocketRequest> onCommentSend() {
        return (senderClient, data, ackSender) -> {
            List<String> receiverIds = data.getReceiverIds();
            for (String receiverId : receiverIds) {
                String recipientSocketID = userSocketMap.get(receiverId);
                if (recipientSocketID != null) {
                    SocketIOClient recipientClient = server.getClient(UUID.fromString(recipientSocketID));
                    if (recipientClient != null && recipientClient.isChannelOpen()) {
                        recipientClient.sendEvent("newComment", data);
                        System.out.println("Signal sent to " + receiverId);
                    } else {
                        System.out.println(receiverId + " is not online");
                    }
                } else {
                    System.out.println("Recipient socket not found");
                }
            }
        };
    }
}
