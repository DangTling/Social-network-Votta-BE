package com.example.backend.websocket;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;


@org.springframework.context.annotation.Configuration
public class Config {

    @Value("${socket-server.host}")
    private String host;

    @Value("${socket-server.port}")
    private int port;

    @Bean
    public SocketIOServer socketIoServer() {
        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);
        config.setOrigin("*");

        return new SocketIOServer(config);
    }


}