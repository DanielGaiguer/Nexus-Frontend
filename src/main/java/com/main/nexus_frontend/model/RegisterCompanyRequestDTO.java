package com.main.nexus_frontend.model;

public class RegisterCompanyRequestDTO{
    private String email;
    private String password;
    private String companyName;
    private String taxId;
    private String phone;
    private String city;
    private String description;

    public RegisterCompanyRequestDTO() {
    }

    public RegisterCompanyRequestDTO(String email, String password, String companyName, String taxId, String phone, String city, String description) {
        this.email = email;
        this.password = password;
        this.companyName = companyName;
        this.taxId = taxId;
        this.phone = phone;
        this.city = city;
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
}
    
