package com.main.nexus_frontend.model;

import java.util.List;

public class ProfessionalProfileDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String city;
    private String state;
    private String cep;
    private Boolean available;
    private Double reputation;
    private Double latitude;
    private Double longitude;
    private List<String> skills;
    private List<String> preferredTypes;
    private String experienceLevel;
    private String profilePhotoUrl;
    private List<String> preferredOpportunityTypes;
    private Double expectedSalaryCLT;
    private Double expectedSalaryPJ;
    private Double freelanceMinExpectation;
    private Double freelanceMaxExpectation;
    private boolean profileComplete;
    private List<String> missingFields;
    private String resume;
    private String linkedinUrl;
    private String githubUrl;
    private String githubLogin;
    private boolean hasGitHub;
    private List<ProfessionalCredentialDTO> credentials;

    public ProfessionalProfileDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
    public Double getReputation() { return reputation; }
    public void setReputation(Double reputation) { this.reputation = reputation; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public List<String> getPreferredTypes() { return preferredTypes; }
    public void setPreferredTypes(List<String> preferredTypes) { this.preferredTypes = preferredTypes; }
    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }
    public List<String> getPreferredOpportunityTypes() { return preferredOpportunityTypes; }
    public void setPreferredOpportunityTypes(List<String> preferredOpportunityTypes) { this.preferredOpportunityTypes = preferredOpportunityTypes; }
    public Double getExpectedSalaryCLT() { return expectedSalaryCLT; }
    public void setExpectedSalaryCLT(Double expectedSalaryCLT) { this.expectedSalaryCLT = expectedSalaryCLT; }
    public Double getExpectedSalaryPJ() { return expectedSalaryPJ; }
    public void setExpectedSalaryPJ(Double expectedSalaryPJ) { this.expectedSalaryPJ = expectedSalaryPJ; }
    public Double getFreelanceMinExpectation() { return freelanceMinExpectation; }
    public void setFreelanceMinExpectation(Double freelanceMinExpectation) { this.freelanceMinExpectation = freelanceMinExpectation; }
    public Double getFreelanceMaxExpectation() { return freelanceMaxExpectation; }
    public void setFreelanceMaxExpectation(Double freelanceMaxExpectation) { this.freelanceMaxExpectation = freelanceMaxExpectation; }
    public boolean isProfileComplete() { return profileComplete; }
    public void setProfileComplete(boolean profileComplete) { this.profileComplete = profileComplete; }
    public List<String> getMissingFields() { return missingFields; }
    public void setMissingFields(List<String> missingFields) { this.missingFields = missingFields; }
    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }
    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }
    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }
    public String getGithubLogin() { return githubLogin; }
    public void setGithubLogin(String githubLogin) { this.githubLogin = githubLogin; }
    public boolean isHasGitHub() { return hasGitHub; }
    public void setHasGitHub(boolean hasGitHub) { this.hasGitHub = hasGitHub; }
    public List<ProfessionalCredentialDTO> getCredentials() { return credentials; }
    public void setCredentials(List<ProfessionalCredentialDTO> credentials) { this.credentials = credentials; }
}
