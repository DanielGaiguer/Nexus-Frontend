package com.main.nexus_frontend.model;

import java.util.List;

public class UpdateProfessionalDTO {
    private String name;
    private String phone;
    private String cep;
    private Double minimumSalary;
    private Double maximumSalary;
    private Boolean available;
    private List<String> preferredTypes;
    private String experienceLevel;

    public UpdateProfessionalDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public Double getMinimumSalary() { return minimumSalary; }
    public void setMinimumSalary(Double minimumSalary) { this.minimumSalary = minimumSalary; }
    public Double getMaximumSalary() { return maximumSalary; }
    public void setMaximumSalary(Double maximumSalary) { this.maximumSalary = maximumSalary; }
    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
    public List<String> getPreferredTypes() { return preferredTypes; }
    public void setPreferredTypes(List<String> preferredTypes) { this.preferredTypes = preferredTypes; }
    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
}
