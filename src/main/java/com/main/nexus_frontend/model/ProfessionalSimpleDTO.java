package com.main.nexus_frontend.model;

public class ProfessionalSimpleDTO {
    private Long id;
    private String name;
    private Double reputation;

    public ProfessionalSimpleDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getReputation() { return reputation; }
    public void setReputation(Double reputation) { this.reputation = reputation; }
}
