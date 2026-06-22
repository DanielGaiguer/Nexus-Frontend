package com.main.nexus_frontend.model;

public class RegisterProfessionalRequestDTO{
    private String email;
    private String password;
    private String name;
    private String phone;
    private String city;
    private Double minimumSalary;
    private Double maximumSalary;

    public RegisterProfessionalRequestDTO() {
    }

    public RegisterProfessionalRequestDTO(String email, String password, String name, String phone, String city, Double minimumSalary, Double maximumSalary) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.city = city;
        this.minimumSalary = minimumSalary;
        this.maximumSalary = maximumSalary;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Double getMinimumSalary() {
        return minimumSalary;
    }

    public void setMinimumSalary(Double minimumSalary) {
        this.minimumSalary = minimumSalary;
    }

    public Double getMaximumSalary() {
        return maximumSalary;
    }

    public void setMaximumSalary(Double maximumSalary) {
        this.maximumSalary = maximumSalary;
    }
    
    
}

