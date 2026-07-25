package com.main.nexus_frontend.model;

public class CompanyAcceptanceRateDTO {
    private Long companyId;
    private String companyName;
    private Integer totalMatches;
    private Integer confirmedMatches;
    private Integer rejectedMatches;
    private Double acceptanceRate;
    private Double averageScore;

    public CompanyAcceptanceRateDTO() {}

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public Double getAcceptanceRate() {
        return acceptanceRate;
    }

    public void setAcceptanceRate(Double acceptanceRate) {
        this.acceptanceRate = acceptanceRate;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }
}
