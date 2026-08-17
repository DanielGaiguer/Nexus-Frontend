package com.main.nexus_frontend.model;

public class ProfessionalCredentialDTO {
    private Long id;
    private String type;   // "CERTIFICATE" ou "EVENT"
    private String name;
    private String color;  // NEXUS, SLATE, CIANO, VIOLETA, TEAL, AMBAR, ROSA, ESMERALDA

    public ProfessionalCredentialDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
