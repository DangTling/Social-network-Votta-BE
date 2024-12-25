//package com.example.backend.websocket;
//
//import com.corundumstudio.socketio.SocketIOClient;
//import com.corundumstudio.socketio.SocketIOServer;
//import com.corundumstudio.socketio.annotation.OnConnect;
//import com.corundumstudio.socketio.annotation.OnDisconnect;
//import com.corundumstudio.socketio.annotation.OnEvent;
//import com.example.backend.models.Message;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//import java.util.UUID;
//
//@Component
//public class SocketIOEventHandle {
//    private final SocketIOServer server;
//    private Map<String, String> userSocketMap = new HashMap<>();
//
//    @Autowired
//    public SocketIOEventHandle(SocketIOServer server) {
//        this.server = server;
//    }
//
//    @OnConnect
//    public void onConnect(SocketIOClient client) {
//        String userId = client.getHandshakeData().getSingleUrlParam("userId");
//        System.out.println("Connected to " + userId);
//        if (userId != null && !userId.equals("undefined")) {
//            userSocketMap.put(userId, client.getSessionId().toString());
//        }
//        server.getBroadcastOperations().sendEvent("getOnlineUsers", userSocketMap.keySet());
//    }
//
//    @OnDisconnect
//    public void onDisconnect(SocketIOClient client) {
//        String userId = client.getHandshakeData().getSingleUrlParam("userId");
//        userSocketMap.remove(userId);
//        server.getBroadcastOperations().sendEvent("getOnlineUsers", userSocketMap.keySet());
//    }
//
//    public String getRecipientSocketId(String recipientId) {
//        return userSocketMap.get(recipientId);
//    }
//
//    @OnEvent("sendMessage")
//    public void onSendMessage(SocketIOClient client, Map<String, Object> data) {
//            System.out.println("Sending message to ");
//        Message message = (Message) data.get("message");
//        String recipientId = (String) data.get("receiverId");
//
//        String recipientSocketId = getRecipientSocketId(recipientId);
//        if (recipientSocketId != null && !recipientSocketId.equals("undefined")) {
//            sendMessageToClient(recipientSocketId, "newMessage", message);
//        } else {
//            System.out.println("No socket id found");
//        }
//    }
//
//    public void sendMessageToClient(String socketId, String event, Message message) {
//        UUID clientUUID = UUID.fromString(socketId);
//        SocketIOClient client = server.getClient(clientUUID);
//        if (client != null) {
//            System.out.println("Sending " + event + " to " + socketId);
//            client.sendEvent(event, message);
//        } else {
//            System.out.println("Client with Socket Id: " + socketId + " is not connected");
//        }
//    }
//}
