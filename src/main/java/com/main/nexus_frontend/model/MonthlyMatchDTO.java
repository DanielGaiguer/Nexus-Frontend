package com.main.nexus_frontend.model;

public class MonthlyMatchDTO {
    private Integer year;
    private Integer month;
    private String monthLabel;
    private Integer totalMatches;
    private Integer confirmedMatches;
    private Integer rejectedMatches;

    public MonthlyMatchDTO() {}

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public String getMonthLabel() {
        return monthLabel;
    }

    public void setMonthLabel(String monthLabel) {
        this.monthLabel = monthLabel;
    }

    public Integer getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(Integer totalMatches) {
        this.totalMatches = totalMatches;
    }

    public Integer getConfirmedMatches() {
        return confirmedMatches;
    }

    public void setConfirmedMatches(Integer confirmedMatches) {
        this.confirmedMatches = confirmedMatches;
    }

    public Integer getRejectedMatches() {
        return rejectedMatches;
    }

    public void setRejectedMatches(Integer rejectedMatches) {
        this.rejectedMatches = rejectedMatches;
    }

   
    
}
