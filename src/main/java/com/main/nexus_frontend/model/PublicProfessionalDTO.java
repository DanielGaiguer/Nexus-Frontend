package com.main.nexus_frontend.model;

import java.util.List;

public class PublicProfessionalDTO {
    private Long id;
    private String name;
    private String city;
    private String uf;
    private String experienceLevel;
    private Double reputation;
    private Boolean available;
    private List<String> skills;
    private List<PublicProjectDTO> previousProjects;
    private Double overallScore;
    private ReputationDTO reputationDetails;
    private String profilePhotoUrl;
    private Double minimumSalary;
    private Double maximumSalary;
    private List<String> preferredTypes;
    private String githubUrl;
    private String githubLogin;
    private boolean hasGitHub;
    private List<ProfessionalCredentialDTO> credentials;

    public PublicProfessionalDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }

    public Double getReputation() { return reputation; }
    public void setReputation(Double reputation) { this.reputation = reputation; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public List<PublicProjectDTO> getPreviousProjects() { return previousProjects; }
    public void setPreviousProjects(List<PublicProjectDTO> previousProjects) { this.previousProjects = previousProjects; }

    public Double getOverallScore() { return overallScore; }
    public void setOverallScore(Double overallScore) { this.overallScore = overallScore; }

    public ReputationDTO getReputationDetails() { return reputationDetails; }
    public void setReputationDetails(ReputationDTO reputationDetails) { this.reputationDetails = reputationDetails; }

    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }

    public Double getMinimumSalary() { return minimumSalary; }
    public void setMinimumSalary(Double minimumSalary) { this.minimumSalary = minimumSalary; }

    public Double getMaximumSalary() { return maximumSalary; }
    public void setMaximumSalary(Double maximumSalary) { this.maximumSalary = maximumSalary; }

    public List<String> getPreferredTypes() { return preferredTypes; }
    public void setPreferredTypes(List<String> preferredTypes) { this.preferredTypes = preferredTypes; }

    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }

    public String getGithubLogin() { return githubLogin; }
    public void setGithubLogin(String githubLogin) { this.githubLogin = githubLogin; }

    public boolean isHasGitHub() { return hasGitHub; }
    public void setHasGitHub(boolean hasGitHub) { this.hasGitHub = hasGitHub; }
    public List<ProfessionalCredentialDTO> getCredentials() { return credentials; }
    public void setCredentials(List<ProfessionalCredentialDTO> credentials) { this.credentials = credentials; }
}
