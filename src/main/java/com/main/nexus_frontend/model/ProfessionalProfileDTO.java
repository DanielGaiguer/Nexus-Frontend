package com.main.nexus_frontend.model;

import java.util.List;

public class ProfessionalProfileDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String city;
    private String uf;
    private String cep;
    private Double minimumSalary;
    private Double maximumSalary;
    private Boolean available;
    private Double reputation;
    private Double latitude;
    private Double longitude;
    private List<String> skills;
    private List<String> preferredTypes;
    private String experienceLevel;
    private String resume;
    private String profilePhotoUrl;

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
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public Double getMinimumSalary() { return minimumSalary; }
    public void setMinimumSalary(Double minimumSalary) { this.minimumSalary = minimumSalary; }
    public Double getMaximumSalary() { return maximumSalary; }
    public void setMaximumSalary(Double maximumSalary) { this.maximumSalary = maximumSalary; }
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
    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }
    
    
}
