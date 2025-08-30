package com.enershare.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Autowired
    private final ObjectMapper objectMapper;

    public MyWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("Native WebSocket connected: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        sessions.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        System.out.println("Received: " + message.getPayload());
        session.sendMessage(new TextMessage("Echo: " + message.getPayload()));
    }

    // ✅ Call this method from anywhere (e.g., service or controller)
    public void broadcastMap(Map<String,String> payload) {
        String jsonString = this.toJsonString(payload);
        this.broadcastJson(jsonString);
    }

    public void broadcastObject(Object payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            this.broadcastJson(json);
        } catch (JsonProcessingException e) {
            // log and return; avoid sending partial/invalid JSON
            // logger.warn("Failed to serialize payload", e);
        }
    }

    // ✅ Call this method from anywhere (e.g., service or controller)
    public void broadcastJson(String jsonPayload) {
        sessions.forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(jsonPayload));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String toJsonString(Map<String, String> map) {
        StringBuilder jsonBuilder = new StringBuilder("{");
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            jsonBuilder
                    .append("\"")
                    .append(entry.getKey())
                    .append("\":\"")
                    .append(entry.getValue())
                    .append("\"");

            if (iterator.hasNext()) {
                jsonBuilder.append(",");
            }
        }

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }

}
