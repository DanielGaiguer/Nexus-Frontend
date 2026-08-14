package com.main.nexus_frontend.model;

import java.util.List;

public class MapOpportunityDTO {
    private Long id;
    private String opportunityType;
    private String title;
    private String companyName;
    private String city;
    private String uf;
    private Double latitude;
    private Double longitude;
    private String workMode;
    private List<String> requiredSkills;
    private String experienceLevel;
    private String projectType;
    private String contractType;
    private Double monthlySalaryMin;
    private Double monthlySalaryMax;
    private Double minimumBudget;
    private Double maximumBudget;
    // String (não LocalDateTime): o Jackson embutido no serializador de
    // "th:inline=javascript" do Thymeleaf não tem o JavaTimeModule
    // registrado, então serializar um LocalDateTime aqui lança exceção NO
    // MEIO do corpo da resposta HTTP (chunked já parcialmente enviado) —
    // a conexão é abortada e o navegador nunca recebe o restante da página
    // (TomSelect, Leaflet, nexus-map.js), fazendo todos os filtros do mapa
    // caírem para o <select> nativo do navegador. String evita o problema
    // e ainda funciona no `new Date(...)` usado em nexus-map.js.
    private String createdAt;

    public MapOpportunityDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(String opportunityType) {
        this.opportunityType = opportunityType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getWorkMode() {
        return workMode;
    }

    public void setWorkMode(String workMode) {
        this.workMode = workMode;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public Double getMonthlySalaryMin() {
        return monthlySalaryMin;
    }

    public void setMonthlySalaryMin(Double monthlySalaryMin) {
        this.monthlySalaryMin = monthlySalaryMin;
    }

    public Double getMonthlySalaryMax() {
        return monthlySalaryMax;
    }

    public void setMonthlySalaryMax(Double monthlySalaryMax) {
        this.monthlySalaryMax = monthlySalaryMax;
    }

    public Double getMinimumBudget() {
        return minimumBudget;
    }

    public void setMinimumBudget(Double minimumBudget) {
        this.minimumBudget = minimumBudget;
    }

    public Double getMaximumBudget() {
        return maximumBudget;
    }

    public void setMaximumBudget(Double maximumBudget) {
        this.maximumBudget = maximumBudget;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
