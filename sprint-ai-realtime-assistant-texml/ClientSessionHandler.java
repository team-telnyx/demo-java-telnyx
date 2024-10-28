package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.tyrus.client.ClientManager;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import javax.websocket.*;
import java.net.URI;
import java.util.*;

public class ClientSessionHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final WebSocketSession telnyxSession;
    private Session openaiSession;
    private final List<String> LOG_EVENT_TYPES = Arrays.asList(
            "response.content.done",
            "rate_limits.updated",
            "response.done",
            "input_audio_buffer.committed",
            "input_audio_buffer.speech_stopped",
            "input_audio_buffer.speech_started",
            "session.created"
    );

    public ClientSessionHandler(WebSocketSession telnyxSession) {
        this.telnyxSession = telnyxSession;
    }

    public void connectToOpenAI() throws Exception {
        ClientEndpointConfig.Configurator configurator = new ClientEndpointConfig.Configurator() {
            @Override
            public void beforeRequest(Map<String, List<String>> headers) {
                headers.put("Authorization", Collections.singletonList("Bearer " + Application.OPENAI_API_KEY));
                headers.put("OpenAI-Beta", Collections.singletonList("realtime=v1"));
            }
        };

        ClientEndpointConfig clientConfig = ClientEndpointConfig.Builder.create()
                .configurator(configurator)
                .build();

        ClientManager client = ClientManager.createClient();

        openaiSession = client.connectToServer(new Endpoint() {
            @Override
            public void onOpen(Session session, EndpointConfig config) {
                try {
                    sendSessionUpdate(session);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                session.addMessageHandler((Whole<String>) message -> {
                    try {
                        handleOpenAIMessage(message);
                    } catch (Exception e) {
                        System.out.println("Error processing OpenAI message: " + e.getMessage() + " Raw message: " + message);
                    }
                });
            }
        }, clientConfig, new URI("wss://api.openai.com/v1/realtime?model=gpt-4o-realtime-preview-2024-10-01"));
    }

    private void sendSessionUpdate(Session session) throws Exception {
        Map<String, Object> sessionUpdate = new HashMap<>();
        sessionUpdate.put("type", "session.update");

        Map<String, Object> sessionMap = new HashMap<>();
        Map<String, Object> turnDetection = new HashMap<>();
        turnDetection.put("type", "server_vad");
        sessionMap.put("turn_detection", turnDetection);
        sessionMap.put("input_audio_format", "g711_ulaw");
        sessionMap.put("output_audio_format", "g711_ulaw");
        sessionMap.put("voice", Application.VOICE);
        sessionMap.put("instructions", Application.SYSTEM_MESSAGE);
        sessionMap.put("modalities", Arrays.asList("text", "audio"));
        sessionMap.put("temperature", 0.8);

        sessionUpdate.put("session", sessionMap);

        String sessionUpdateStr = objectMapper.writeValueAsString(sessionUpdate);
        System.out.println("Sending session update: " + sessionUpdateStr);

        session.getAsyncRemote().sendText(sessionUpdateStr);
    }

    private void handleOpenAIMessage(String message) throws Exception {
        Map<String, Object> response = objectMapper.readValue(message, Map.class);
        String type = (String) response.get("type");
        if (LOG_EVENT_TYPES.contains(type)) {
            System.out.println("Received event: " + type + " " + response);
        }
        if ("session.updated".equals(type)) {
            System.out.println("Session updated successfully: " + response);
        }
        if ("response.audio.delta".equals(type) && response.get("delta") != null) {
            Map<String, Object> audioDelta = new HashMap<>();
            audioDelta.put("event", "media");
            Map<String, Object> media = new HashMap<>();
            media.put("payload", response.get("delta"));
            audioDelta.put("media", media);
            String audioDeltaStr = objectMapper.writeValueAsString(audioDelta);
            synchronized (telnyxSession) {
                if (telnyxSession.isOpen()) {
                    telnyxSession.sendMessage(new TextMessage(audioDeltaStr));
                }
            }
        }
    }

    public void handleTelnyxMessage(String data) throws Exception {
        Map<String, Object> msg = objectMapper.readValue(data, Map.class);
        String eventType = (String) msg.get("event");

        if ("media".equals(eventType)) {
            if (openaiSession != null && openaiSession.isOpen()) {
                Map<String, Object> audioAppend = new HashMap<>();
                audioAppend.put("type", "input_audio_buffer.append");
                audioAppend.put("audio", ((Map<String, Object>) msg.get("media")).get("payload"));
                String audioAppendStr = objectMapper.writeValueAsString(audioAppend);
                openaiSession.getAsyncRemote().sendText(audioAppendStr);
            }
        } else if ("start".equals(eventType)) {
            String streamSid = (String) msg.get("stream_id");
            System.out.println("Incoming stream has started: " + streamSid);
        } else {
            System.out.println("Received non-media event: " + eventType);
        }
    }

    public void closeSessions() throws Exception {
        if (telnyxSession.isOpen()) {
            telnyxSession.close();
        }
        if (openaiSession != null && openaiSession.isOpen()) {
            openaiSession.close();
        }
    }
}