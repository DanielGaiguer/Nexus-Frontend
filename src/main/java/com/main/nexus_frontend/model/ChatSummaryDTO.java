package com.main.nexus_frontend.model;

import java.time.LocalDateTime;

public class ChatSummaryDTO {
    private Long matchId;
    private String otherPartyName;
    private String otherPartyPhotoUrl;
    private String otherPartyType;
    private String projectTitle;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private long unreadCount;
    private Boolean matchActive;
    private Integer daysUntilExpiration;

    public ChatSummaryDTO() {}

    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }

    public String getOtherPartyName() { return otherPartyName; }
    public void setOtherPartyName(String otherPartyName) { this.otherPartyName = otherPartyName; }

    public String getOtherPartyPhotoUrl() { return otherPartyPhotoUrl; }
    public void setOtherPartyPhotoUrl(String otherPartyPhotoUrl) { this.otherPartyPhotoUrl = otherPartyPhotoUrl; }

    public String getOtherPartyType() { return otherPartyType; }
    public void setOtherPartyType(String otherPartyType) { this.otherPartyType = otherPartyType; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(LocalDateTime lastMessageAt) { this.lastMessageAt = lastMessageAt; }

    public long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(long unreadCount) { this.unreadCount = unreadCount; }

    public Boolean getMatchActive() { return matchActive; }
    public void setMatchActive(Boolean matchActive) { this.matchActive = matchActive; }

    public Integer getDaysUntilExpiration() { return daysUntilExpiration; }
    public void setDaysUntilExpiration(Integer daysUntilExpiration) { this.daysUntilExpiration = daysUntilExpiration; }
}
