package com.main.nexus_frontend.model;

public class SkillGapDTO {
    private String skillName;
    private String category;
    private Long frequency;

    public SkillGapDTO() {}

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

    public Long getFrequency() {
        return frequency;
    }

    public void setFrequency(Long frequency) {
        this.frequency = frequency;
    }
}
