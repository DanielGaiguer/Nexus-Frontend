package com.main.nexus_frontend.model;

public class UserSummaryDTO {
    private Long id;
    private String email;
    private String type;
    private Boolean active;

    public UserSummaryDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
