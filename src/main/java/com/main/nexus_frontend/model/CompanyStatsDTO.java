package com.main.nexus_frontend.model;

public class CompanyStatsDTO {
    private Integer totalProjects;
    private Integer confirmedMatches;

    public CompanyStatsDTO() {}

    public Integer getTotalProjects() { return totalProjects; }
    public void setTotalProjects(Integer totalProjects) { this.totalProjects = totalProjects; }

    public Integer getConfirmedMatches() { return confirmedMatches; }
    public void setConfirmedMatches(Integer confirmedMatches) { this.confirmedMatches = confirmedMatches; }
}
