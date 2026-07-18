package com.main.nexus_frontend.model;

public class MonthlyDataDTO {
    private String label;
    private Integer value;

    public MonthlyDataDTO() {}

    public MonthlyDataDTO(String label, Integer value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }
}
