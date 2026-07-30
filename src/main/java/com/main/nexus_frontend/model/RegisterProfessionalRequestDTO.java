package com.main.nexus_frontend.model;

public class RegisterProfessionalRequestDTO{
    private String email;
    private String password;
    private String name;
    private String phone;
    private String cep;
    private Double expectedSalaryCLT;
    private Double expectedSalaryPJ;
    private Double freelanceMinExpectation;
    private Double freelanceMaxExpectation;

    public RegisterProfessionalRequestDTO() {
    }

    public RegisterProfessionalRequestDTO(String email, String password, String name, String phone, String cep, Double expectedSalaryCLT, Double expectedSalaryPJ, Double freelanceMinExpectation, Double freelanceMaxExpectation) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.cep = cep;
        this.expectedSalaryCLT = expectedSalaryCLT;
        this.expectedSalaryPJ = expectedSalaryPJ;
        this.freelanceMinExpectation = freelanceMinExpectation;
        this.freelanceMaxExpectation = freelanceMaxExpectation;
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

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Double getExpectedSalaryCLT() {
        return expectedSalaryCLT;
    }

    public void setExpectedSalaryCLT(Double expectedSalaryCLT) {
        this.expectedSalaryCLT = expectedSalaryCLT;
    }

    public Double getExpectedSalaryPJ() {
        return expectedSalaryPJ;
    }

    public void setExpectedSalaryPJ(Double expectedSalaryPJ) {
        this.expectedSalaryPJ = expectedSalaryPJ;
    }

    public Double getFreelanceMinExpectation() {
        return freelanceMinExpectation;
    }

    public void setFreelanceMinExpectation(Double freelanceMinExpectation) {
        this.freelanceMinExpectation = freelanceMinExpectation;
    }

    public Double getFreelanceMaxExpectation() {
        return freelanceMaxExpectation;
    }

    public void setFreelanceMaxExpectation(Double freelanceMaxExpectation) {
        this.freelanceMaxExpectation = freelanceMaxExpectation;
    }

}

