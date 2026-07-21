package com.main.nexus_frontend.model;

import java.util.List;

public class NotificationSummaryDTO {
    private Integer unreadCount;
    private List<NotificationDTO> notifications;

    public NotificationSummaryDTO() {}

    public Integer getUnreadCount() { return unreadCount; }
    public void setUnreadCount(Integer unreadCount) { this.unreadCount = unreadCount; }
    public List<NotificationDTO> getNotifications() { return notifications; }
    public void setNotifications(List<NotificationDTO> notifications) { this.notifications = notifications; }
}
