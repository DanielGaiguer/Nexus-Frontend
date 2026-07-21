package com.main.nexus_frontend.model;

public class ProfessionalDashboardDTO {
    private ProfessionalProfileDTO profile;
    private Integer totalProjects;
    private Integer confirmedMatches;

    public ProfessionalDashboardDTO() {}

    public ProfessionalProfileDTO getProfile() { return profile; }
    public void setProfile(ProfessionalProfileDTO profile) { this.profile = profile; }
    public Integer getTotalProjects() { return totalProjects; }
    public void setTotalProjects(Integer totalProjects) { this.totalProjects = totalProjects; }
    public Integer getConfirmedMatches() { return confirmedMatches; }
    public void setConfirmedMatches(Integer confirmedMatches) { this.confirmedMatches = confirmedMatches; }
}
