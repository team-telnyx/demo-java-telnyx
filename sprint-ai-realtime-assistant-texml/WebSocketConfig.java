package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final MediaStreamHandler mediaStreamHandler;

    public WebSocketConfig(MediaStreamHandler mediaStreamHandler) {
        this.mediaStreamHandler = mediaStreamHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(mediaStreamHandler, "/media-stream").setAllowedOrigins("*");
    }
}