package com.main.nexus_frontend.model;

public class ScoreDistributionDTO {
    private String range;
    private Integer count;
    private Double percentage;

    public ScoreDistributionDTO() {}

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

   
}
