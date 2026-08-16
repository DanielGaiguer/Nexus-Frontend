package com.main.nexus_frontend.model;

public class SoftSkillFeedbackDTO {
    private String reason;
    private String enumValue;
    private Long count;

    public SoftSkillFeedbackDTO() {}

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getEnumValue() {
        return enumValue;
    }

    public void setEnumValue(String enumValue) {
        this.enumValue = enumValue;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
