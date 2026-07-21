package com.main.nexus_frontend.model;

import java.time.LocalDateTime;

public class MatchHistoryDTO {
    private String fromStatus;
    private String toStatus;
    private String changedBy;
    private LocalDateTime changedAt;

    public MatchHistoryDTO() {}

    public String getFromStatus() { return fromStatus; }
    public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }

    public String getToStatus() { return toStatus; }
    public void setToStatus(String toStatus) { this.toStatus = toStatus; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
}
