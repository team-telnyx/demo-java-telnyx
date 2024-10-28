package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.tyrus.client.ClientManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import javax.websocket.*;
import javax.websocket.MessageHandler.Whole;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MediaStreamHandler extends TextWebSocketHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, ClientSessionHandler> sessionHandlers = new ConcurrentHashMap<>();
    private final List<String> LOG_EVENT_TYPES = Arrays.asList(
            "response.content.done",
            "rate_limits.updated",
            "response.done",
            "input_audio_buffer.committed",
            "input_audio_buffer.speech_stopped",
            "input_audio_buffer.speech_started",
            "session.created"
    );

    @Override
    public void afterConnectionEstablished(WebSocketSession telnyxSession) throws Exception {
        System.out.println("Client connected");

        ClientSessionHandler clientHandler = new ClientSessionHandler(telnyxSession);
        sessionHandlers.put(telnyxSession.getId(), clientHandler);
        clientHandler.connectToOpenAI();
    }

    @Override
    protected void handleTextMessage(WebSocketSession telnyxSession, TextMessage message) throws Exception {
        String data = message.getPayload();
        ClientSessionHandler clientHandler = sessionHandlers.get(telnyxSession.getId());
        if (clientHandler != null) {
            clientHandler.handleTelnyxMessage(data);
        } else {
            System.out.println("No client handler found for session " + telnyxSession.getId());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession telnyxSession, CloseStatus status) throws Exception {
        System.out.println("Client disconnected.");
        ClientSessionHandler clientHandler = sessionHandlers.remove(telnyxSession.getId());
        if (clientHandler != null) {
            clientHandler.closeSessions();
        }
    }
}