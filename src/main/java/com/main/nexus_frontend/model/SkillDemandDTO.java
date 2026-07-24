package com.main.nexus_frontend.model;

public class SkillDemandDTO {
    private String skillName;
    private String category;
    private Integer projectCount;

    public SkillDemandDTO() {}

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Integer projectCount) {
        this.projectCount = projectCount;
    }

 
}
