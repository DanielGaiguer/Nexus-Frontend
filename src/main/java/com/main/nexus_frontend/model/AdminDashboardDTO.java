package com.main.nexus_frontend.model;

public class AdminDashboardDTO {
    private Integer totalUsers;
    private Integer totalProjects;
    private Integer totalMatches;
    private Integer pendingCompanies;

    public AdminDashboardDTO() {}

    public Integer getTotalUsers() { return totalUsers; }
    public void setTotalUsers(Integer totalUsers) { this.totalUsers = totalUsers; }
    public Integer getTotalProjects() { return totalProjects; }
    public void setTotalProjects(Integer totalProjects) { this.totalProjects = totalProjects; }
    public Integer getTotalMatches() { return totalMatches; }
    public void setTotalMatches(Integer totalMatches) { this.totalMatches = totalMatches; }
    public Integer getPendingCompanies() { return pendingCompanies; }
    public void setPendingCompanies(Integer pendingCompanies) { this.pendingCompanies = pendingCompanies; }
}
