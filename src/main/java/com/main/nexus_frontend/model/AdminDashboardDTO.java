package com.main.nexus_frontend.model;

import java.util.List;

public class AdminDashboardDTO {
    private Integer totalUsers;
    private Integer totalProfessionals;
    private Integer totalCompanies;
    private Integer totalProjects;
    private Integer totalOpenProjects;
    private Integer totalMatches;
    private Integer totalConfirmedMatches;
    private Double  matchConversionRate;
    private Integer pendingCompanies;
    private List<MonthlyDataDTO> monthlyMatches;

    public AdminDashboardDTO() {}

    public Integer getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Integer totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Integer getTotalProfessionals() {
        return totalProfessionals;
    }

    public void setTotalProfessionals(Integer totalProfessionals) {
        this.totalProfessionals = totalProfessionals;
    }

    public Integer getTotalCompanies() {
        return totalCompanies;
    }

    public void setTotalCompanies(Integer totalCompanies) {
        this.totalCompanies = totalCompanies;
    }

    public Integer getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(Integer totalProjects) {
        this.totalProjects = totalProjects;
    }

    public Integer getTotalOpenProjects() {
        return totalOpenProjects;
    }

    public void setTotalOpenProjects(Integer totalOpenProjects) {
        this.totalOpenProjects = totalOpenProjects;
    }

    public Integer getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(Integer totalMatches) {
        this.totalMatches = totalMatches;
    }

    public Integer getTotalConfirmedMatches() {
        return totalConfirmedMatches;
    }

    public void setTotalConfirmedMatches(Integer totalConfirmedMatches) {
        this.totalConfirmedMatches = totalConfirmedMatches;
    }

    public Double getMatchConversionRate() {
        return matchConversionRate;
    }

    public void setMatchConversionRate(Double matchConversionRate) {
        this.matchConversionRate = matchConversionRate;
    }

    public Integer getPendingCompanies() {
        return pendingCompanies;
    }

    public void setPendingCompanies(Integer pendingCompanies) {
        this.pendingCompanies = pendingCompanies;
    }

    public List<MonthlyDataDTO> getMonthlyMatches() {
        return monthlyMatches;
    }

    public void setMonthlyMatches(List<MonthlyDataDTO> monthlyMatches) {
        this.monthlyMatches = monthlyMatches;
    }

}
