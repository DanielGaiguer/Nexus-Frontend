package com.main.nexus_frontend.model;

public class ProjectStatusDistributionDTO {
    private String status;
    private String enumValue;
    private Long count;

    public ProjectStatusDistributionDTO() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
