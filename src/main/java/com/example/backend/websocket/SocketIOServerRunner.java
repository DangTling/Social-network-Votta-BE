//package com.example.backend.websocket;
//
//import com.corundumstudio.socketio.SocketIOServer;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class SocketIOServerRunner implements CommandLineRunner {
//
//    private final SocketIOServer server;
//
//    public SocketIOServerRunner(SocketIOServer server) {
//        this.server = server;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        server.start();
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            System.out.println("Stopping Socket.IO server...");
//            server.stop();
//        }));
//    }
//}
