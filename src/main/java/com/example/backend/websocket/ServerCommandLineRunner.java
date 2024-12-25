package com.example.backend.websocket;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@Component
public class ServerCommandLineRunner implements CommandLineRunner {

    @Autowired
    private final SocketIOServer server;

    public ServerCommandLineRunner(SocketIOServer server) {
        this.server = server;
    }

//    public static void main(String[] args) {
//        SpringApplication.run(ServerCommandLineRunner.class, args);
//    }

    @Override
    public void run(String... args) throws Exception {
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }
}

