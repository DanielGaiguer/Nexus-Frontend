package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.ChatSummaryDTO;
import com.main.nexus_frontend.model.MessageDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ChatBffService {

    private final RestClient restClient;

    public ChatBffService(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public List<ChatSummaryDTO> getMatches(String jwt) {
        ChatSummaryDTO[] response = restClient.get()
                .uri("/chat/matches")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load chats");
                })
                .body(ChatSummaryDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public List<MessageDTO> getMessages(Long matchId, String jwt) {
        MessageDTO[] response = restClient.get()
                .uri("/chat/{matchId}/messages", matchId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load messages");
                })
                .body(MessageDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public Long getUnreadTotal(String jwt) {
        Long response = restClient.get()
                .uri("/chat/unread-total")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load unread total");
                })
                .body(Long.class);
        return response != null ? response : 0L;
    }
}
