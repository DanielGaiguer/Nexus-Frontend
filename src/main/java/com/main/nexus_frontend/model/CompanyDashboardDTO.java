package com.main.nexus_frontend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompanyDashboardDTO {
    private CompanyProfileDTO profile;
    private Integer totalProjects;
    // O backend expõe este campo como "totalMatches" (ver CompanyDashboardDTO no nexus)
    @JsonProperty("totalMatches")
    private Integer confirmedMatches;

    public CompanyDashboardDTO() {}

    public CompanyProfileDTO getProfile() { return profile; }
    public void setProfile(CompanyProfileDTO profile) { this.profile = profile; }
    public Integer getTotalProjects() { return totalProjects; }
    public void setTotalProjects(Integer totalProjects) { this.totalProjects = totalProjects; }
    public Integer getConfirmedMatches() { return confirmedMatches; }
    public void setConfirmedMatches(Integer confirmedMatches) { this.confirmedMatches = confirmedMatches; }
}
