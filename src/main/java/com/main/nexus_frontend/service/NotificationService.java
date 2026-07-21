package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.NotificationSummaryDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class NotificationService {

    private final RestClient restClient;

    public NotificationService(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public NotificationSummaryDTO getAll(String token) {
        NotificationSummaryDTO res = restClient.get()
                .uri("/notifications")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(NotificationSummaryDTO.class);
        return res != null ? res : new NotificationSummaryDTO();
    }

    public Integer getUnreadCount(String token) {
        try {
            Integer count = restClient.get()
                    .uri("/notifications/unread/count")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(Integer.class);
            return count != null ? count : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public void markAllRead(String token) {
        restClient.method(HttpMethod.PUT)
                .uri("/notifications/read-all")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(String.class);
    }

    public void markRead(String token, Long id) {
        restClient.method(HttpMethod.PUT)
                .uri("/notifications/" + id + "/read")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(String.class);
    }
}
